CREATE TABLE IF NOT EXISTS reports.tsp_reports
(
    id             numeric               not null,
    transaction_id numeric               not null,
    category       character varying(50) not null,
    wallet_id      bigint                not null,
    tsp_id         character varying(50) not null,
    created_by     bigint                not null,
    created_at     timestamp             not null,
    version        bigint default 0,
    PRIMARY KEY (id)
);