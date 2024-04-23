CREATE TABLE IF NOT EXISTS `tbl_user_account` (
    `ID_User` CHAR(36) NOT NULL, -- Primary key
    `Username` VARCHAR(255) NOT NULL,
    `Email` VARCHAR(255) UNIQUE NOT NULL,
    `Password` VARCHAR(255) NOT NULL,
    `CreateTime` DATETIME,
    `IsActive` BOOLEAN DEFAULT true,
    PRIMARY KEY (`ID_User`)
);

DELIMITER //
CREATE TRIGGER pre_insert
    BEFORE INSERT ON tbl_user_account
    FOR EACH ROW
BEGIN
    SET NEW.CreateTime = CURRENT_TIMESTAMP,
        NEW.ID_User = UUID();
END;
//
DELIMITER ;
