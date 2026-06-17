-- make DEA certificate optional
UPDATE certificate_types SET optional = true WHERE id = 'DEA';