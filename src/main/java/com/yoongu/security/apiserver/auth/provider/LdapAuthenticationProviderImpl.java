package com.yoongu.security.apiserver.auth.provider;

import com.yoongu.security.apiserver.auth.exception.PasswordExpiredException;
import com.yoongu.security.apiserver.auth.exception.UserExpiredException;

import com.yoongu.security.apiserver.auth.service.UserService;
import java.util.Collections;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.yoongu.security.persistence.auth.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class LdapAuthenticationProviderImpl implements AuthenticationProvider {

    @Value("${authentication.active.directory.url}")
    private String activeDirectoryServerUrl;

    @Value("${authentication.active.directory.rdn}")
    private String activeDirectoryBaseRdn;

    @Value("${authentication.active.directory.service.id}")
    private String activeDirectoryServiceId;

    @Value("${authentication.active.directory.service.domain}")
    private String activeDirectoryDomain;

    @Value("${authentication.active.directory.service.password}")
    private String activeDirectoryServicePassword;

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String userName = token.getName();
        String password = (String) token.getCredentials();
        User user = userService.getUserByUserName(userName);

        try {
            LdapContext ldapContext = connectActiveDirectory();
            boolean isExistsUser = searchUserInActiveDirectory(ldapContext, userName);
            if (!isExistsUser) {
                throw new UsernameNotFoundException("user not exists in active directory");
            }
            loginToActiveDirectory(userName, password);
        } catch (AuthenticationException e) {
            LdapError ldapError = LdapError.getByMessage(e.getMessage());
            handleAuthenticationException(ldapError);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        return new UsernamePasswordAuthenticationToken(user, password, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private LdapContext connectActiveDirectory() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, activeDirectoryServerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, activeDirectoryServiceId);
        env.put(Context.SECURITY_CREDENTIALS, activeDirectoryServicePassword);
        return new InitialLdapContext(env, null);
    }

    private boolean searchUserInActiveDirectory(LdapContext ctx, String userId) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"sn", "givenName", "samAccountName"});

        String searchFilter = String.format("(&(objectClass=user)(sAMAccountName=%s))", userId);
        int userCount = 0;
        NamingEnumeration<SearchResult> results = ctx.search(activeDirectoryBaseRdn, searchFilter, searchControls);

        while (results.hasMoreElements()) {
            SearchResult result = results.next();
            userCount++;
            log.debug("result name : {}", result.getName());
            Attributes attributes = result.getAttributes();
            log.debug("samAccountName : {}", attributes.get("samAccountName"));
        }

        return userCount >= 1;
    }

    private void loginToActiveDirectory(String userId, String password) throws NamingException {
        Hashtable<String, String> usrEnv = new Hashtable<>();
        usrEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        usrEnv.put(Context.PROVIDER_URL, activeDirectoryServerUrl);
        usrEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        usrEnv.put(Context.SECURITY_PRINCIPAL, userId + activeDirectoryDomain);
        usrEnv.put(Context.SECURITY_CREDENTIALS, password);
        new InitialLdapContext(usrEnv, null);
    }

    private void handleAuthenticationException(LdapError ldapError) {
        switch (ldapError) {
            case NOT_FOUND_ACCOUNT:
                throw new UsernameNotFoundException("User not exists in active directory");
            case INVALID_PASSWORD:
                throw new BadCredentialsException("Invalid password");
            case LOCK_ACCOUNT:
                throw new LockedException("Account is Locked. please reset this account from admin");
            case EXPIRED_ACCOUNT:
                throw new UserExpiredException("Expire user login accepted date");
            case EXPIRED_PASSWORD:
                throw new PasswordExpiredException("expire this user's password. please reset this account from admin");
            default:
                throw new BadCredentialsException("unknown authentication exception");
        }
    }

    private enum LdapError {
        NOT_FOUND_ACCOUNT,
        INVALID_PASSWORD,
        LOCK_ACCOUNT,
        EXPIRED_PASSWORD,
        EXPIRED_ACCOUNT,
        UNKNOWN_ERROR;

        public static LdapError getByMessage(String message) {
            if (message.indexOf("data 525") > 0) {
                return NOT_FOUND_ACCOUNT;
            } else if (message.indexOf("data 52e") > 0) {
                return INVALID_PASSWORD;
            } else if (message.indexOf("data 533") > 0) {
                return LOCK_ACCOUNT;
            } else if (message.indexOf("data 532") > 0 || message.indexOf("data 773") > 0) {
                return EXPIRED_PASSWORD;
            } else if (message.indexOf("data 701") > 0) {
                return EXPIRED_ACCOUNT;
            } else {
                return UNKNOWN_ERROR;
            }
        }
    }

}
