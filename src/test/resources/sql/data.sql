INSERT INTO user (
    is_account_non_expired,
    is_account_non_locked,
    is_credentials_non_expired,
    is_enabled,
    nick_name,
    password,
    role,
    user_name,
    created_date,
    modified_date,
    login_accepted_date
)
VALUES (
   1,
   1,
   1,
   1,
   'miller',
   '{bcrypt}$2a$10$aOdm0jXwLafHAGqpw9m2e.ZeAXWiy9hHD6P9vTs4c3ZEHn8cNqOHO',
   'ROLE_USER',
   'miller',
   now(),
   now(),
   '2023-02-01'
);

INSERT INTO user (
    is_account_non_expired,
    is_account_non_locked,
    is_credentials_non_expired,
    is_enabled,
    nick_name,
    password,
    role,
    user_name,
    created_date,
    modified_date,
    login_accepted_date
)
VALUES (
   1,
   1,
   1,
   1,
   'admin',
   '{bcrypt}$2a$10$aOdm0jXwLafHAGqpw9m2e.ZeAXWiy9hHD6P9vTs4c3ZEHn8cNqOHO',
   'ROLE_ADMIN',
   'admin',
   now(),
   now(),
   '2023-02-01'
);
