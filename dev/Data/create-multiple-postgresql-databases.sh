#!/bin/bash

set -e
set -u

# POSTGRES_MULTIPLE_DATABASES format:
#   db_name,/path/to/script.sql:db_name_2,/path/to/script_2.sql

create_user_and_database() {
	local database
	local script
	database=$(echo "$1" | tr ',' ' ' | awk '{print $1}')
	script=$(echo "$1" | tr ',' ' ' | awk '{print $2}')

	if [ -z "$database" ]; then
		echo "  Skipping empty database entry"
		return
	fi

	echo "  Creating database '$database'"

	local db_exists
	db_exists=$(psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -tAc "SELECT 1 FROM pg_database WHERE datname = '$database';")
	if [ "$db_exists" != "1" ]; then
		psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
			CREATE DATABASE $database;
			GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
	else
		echo "  Database '$database' already exists"
	fi

	if [ -n "$script" ]; then
		echo "  Initializing '$database' with '$script'"
		if [ -f "$script" ]; then
			psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" "$database" -f "$script"
		else
			echo "  SQL script not found: $script"
		fi
	else
		echo "  No initialization script for '$database'"
	fi
}

if [ -n "${POSTGRES_MULTIPLE_DATABASES:-}" ]; then
	echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo "$POSTGRES_MULTIPLE_DATABASES" | tr ':' ' '); do
		create_user_and_database "$db"
	done
	echo "Multiple databases created"
fi
