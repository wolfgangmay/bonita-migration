CREATE TABLE flownode_instance (
  tenantid BIGINT NOT NULL,
  id BIGINT NOT NULL,
  flownodeDefinitionId BIGINT NOT NULL,
  kind VARCHAR(25) NOT NULL,
  rootContainerId BIGINT NOT NULL,
  parentContainerId BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  displayName VARCHAR(255),
  displayDescription VARCHAR(255),
  stateId INT NOT NULL,
  stateName VARCHAR(50),
  prev_state_id INT NOT NULL,
  terminal BOOLEAN NOT NULL,
  stable BOOLEAN ,
  actorId BIGINT NULL,
  assigneeId BIGINT DEFAULT 0 NOT NULL,
  reachedStateDate BIGINT,
  lastUpdateDate BIGINT,
  expectedEndDate BIGINT,
  claimedDate BIGINT,
  priority TINYINT,
  gatewayType VARCHAR(50),
  hitBys VARCHAR(255),
  stateCategory VARCHAR(50) NOT NULL,
  logicalGroup1 BIGINT NOT NULL,
  logicalGroup2 BIGINT NOT NULL,
  logicalGroup3 BIGINT,
  logicalGroup4 BIGINT NOT NULL,
  loop_counter INT,
  loop_max INT,
  description VARCHAR(255),
  sequential BOOLEAN,
  loopDataInputRef VARCHAR(255),
  loopDataOutputRef VARCHAR(255),
  dataInputItemRef VARCHAR(255),
  dataOutputItemRef VARCHAR(255),
  loopCardinality INT,
  nbActiveInst INT,
  nbCompletedInst INT,
  nbTerminatedInst INT,
  executedBy BIGINT,
  executedBySubstitute BIGINT,
  activityInstanceId BIGINT,
  state_executing BOOLEAN DEFAULT FALSE,
  abortedByBoundary BIGINT,
  triggeredByEvent BOOLEAN,
  interrupting BOOLEAN,
  tokenCount INT NOT NULL,
  PRIMARY KEY (tenantid, id)
) ENGINE = INNODB@@
CREATE TABLE arch_flownode_instance (
  tenantid BIGINT NOT NULL,
  id BIGINT NOT NULL,
  flownodeDefinitionId BIGINT NOT NULL,
  kind VARCHAR(25) NOT NULL,
  sourceObjectId BIGINT,
  archiveDate BIGINT NOT NULL,
  rootContainerId BIGINT NOT NULL,
  parentContainerId BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  displayName VARCHAR(255),
  displayDescription VARCHAR(255),
  stateId INT NOT NULL,
  stateName VARCHAR(50),
  terminal BOOLEAN NOT NULL,
  stable BOOLEAN ,
  actorId BIGINT NULL,
  assigneeId BIGINT DEFAULT 0 NOT NULL,
  reachedStateDate BIGINT,
  lastUpdateDate BIGINT,
  expectedEndDate BIGINT,
  claimedDate BIGINT,
  priority TINYINT,
  gatewayType VARCHAR(50),
  hitBys VARCHAR(255),
  logicalGroup1 BIGINT NOT NULL,
  logicalGroup2 BIGINT NOT NULL,
  logicalGroup3 BIGINT,
  logicalGroup4 BIGINT NOT NULL,
  loop_counter INT,
  loop_max INT,
  loopCardinality INT,
  loopDataInputRef VARCHAR(255),
  loopDataOutputRef VARCHAR(255),
  description VARCHAR(255),
  sequential BOOLEAN,
  dataInputItemRef VARCHAR(255),
  dataOutputItemRef VARCHAR(255),
  nbActiveInst INT,
  nbCompletedInst INT,
  nbTerminatedInst INT,
  executedBy BIGINT,
  executedBySubstitute BIGINT,
  activityInstanceId BIGINT,
  aborting BOOLEAN NOT NULL,
  triggeredByEvent BOOLEAN,
  interrupting BOOLEAN,
  PRIMARY KEY (tenantid, id)
) ENGINE = INNODB