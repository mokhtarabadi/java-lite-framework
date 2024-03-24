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
        'ROLE_CUSTOMER',
        'Customer role',
        1703512475304
    ),
    (
        UUID(),
        'ROLE_PROVIDER',
        'Provider role',
        1703512475304
    )