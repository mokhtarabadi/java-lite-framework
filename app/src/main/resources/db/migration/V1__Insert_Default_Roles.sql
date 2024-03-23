INSERT
    INTO
        roles(
            id,
            name,
            description,
            created_at
        )
    VALUES(
        UUID(),
        'ROLE_USER',
        'User role',
        1703512475304
    ),
    (
        UUID(),
        'ROLE_ADMIN',
        'Admin role',
        1703512475304
    )