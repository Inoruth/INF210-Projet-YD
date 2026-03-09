#!/bin/bash

set -e
set -u
#POSTGRES_USER=pguser
#POSTGRES_PASSWORD=pgpwd
#POSTGRES_MULTIPLE_DATABASES="soins_db,/docker-entrypoint-initdb.d/SQL_Scripts/create_db_soins.sql:comrec_db,:gestpers_db,/docker-entrypoint-initdb.d/SQL_Scripts/create_db_gestpers.sql:jobmngt_db,/docker-entrypoint-initdb.d/SQL_Scripts/create_db_jobmngt.sql:bandgateway_db,/docker-entrypoint-initdb.d/SQL_Scripts/create_db_bandgateway.sql"
 

function create_user_and_database() {
	local database=$(echo $1 | tr ',' ' ' | awk  '{print $1}')
	local script=$(echo $1 | tr ',' ' ' | awk  '{print $2}')
	echo "  Creating  database '$database'"

	echo "psql -v ON_ERROR_STOP=1 --username \"$POSTGRES_USER\" <<-EOSQL"
	echo "CREATE DATABASE $database;"
	echo "GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
	EOSQL
	if [ ! -z $script ]; then
		echo "  Initialized with script '$script'"
		if [ -f $script ]; then
			psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" $database -f $script
		else
			echo "Script SQL $script not found"
		fi
	else
		echo "No initialization script for DB $database"
	fi
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ':' ' '); do
		create_user_and_database $db
	done
	echo "Multiple databases created"
fi
