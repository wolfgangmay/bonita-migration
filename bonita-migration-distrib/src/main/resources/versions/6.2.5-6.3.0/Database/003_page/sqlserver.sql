CREATE TABLE page (
  tenantId NUMERIC(19, 0) NOT NULL,
  id NUMERIC(19, 0) NOT NULL,
  name NVARCHAR(50) NOT NULL,
  displayName NVARCHAR(255) NOT NULL,
  description NVARCHAR(MAX),
  installationDate NUMERIC(19, 0) NOT NULL,
  installedBy NUMERIC(19, 0) NOT NULL,
  provided BIT,
  lastModificationDate NUMERIC(19, 0) NOT NULL,
  content VARBINARY(MAX),
  UNIQUE (tenantId, name),
  PRIMARY KEY (tenantId, id)
)
@@
INSERT INTO sequence (tenantid, id, nextid)
	SELECT id, 10120, 1 FROM tenant
	ORDER BY id ASC
@@
ALTER TABLE profileentry ADD custom BIT DEFAULT 0
@@
