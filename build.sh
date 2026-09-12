#!/bin/bash
# ============================================================
# build.sh - Compiles AirRouteX and assembles a ready-to-deploy
# webapp folder at ./webapp, which you copy (or symlink) into
# Tomcat's webapps/ directory as AirRouteX.
#
# Usage:
#   ./build.sh
#   cp -r webapp $CATALINA_HOME/webapps/AirRouteX
# ============================================================
set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
WEBAPP_DIR="$PROJECT_ROOT/webapp"

echo "==> Cleaning previous build..."
rm -rf "$WEBAPP_DIR"
mkdir -p "$WEBAPP_DIR/WEB-INF/classes"
mkdir -p "$WEBAPP_DIR/WEB-INF/lib"

echo "==> Checking for db.properties..."
if [ ! -f "$BACKEND_DIR/db.properties" ]; then
  echo "ERROR: $BACKEND_DIR/db.properties not found."
  echo "Copy backend/db.properties.example to backend/db.properties and fill in your MySQL credentials first."
  exit 1
fi

echo "==> Compiling Java sources..."
# You need servlet-api on the compile classpath, but it must NOT be
# copied into WEB-INF/lib - Tomcat already provides it, and shipping
# your own copy causes classloading conflicts. On most Tomcat 9
# installs it lives at $CATALINA_HOME/lib/servlet-api.jar.
if [ -z "$SERVLET_API_JAR" ]; then
  if [ -n "$CATALINA_HOME" ] && [ -f "$CATALINA_HOME/lib/servlet-api.jar" ]; then
    SERVLET_API_JAR="$CATALINA_HOME/lib/servlet-api.jar"
  else
    echo "ERROR: Could not find servlet-api.jar."
    echo "Set CATALINA_HOME to your Tomcat installation, or set SERVLET_API_JAR"
    echo "to point directly at servlet-api.jar before running this script."
    exit 1
  fi
fi

COMPILE_CP="$SERVLET_API_JAR:$BACKEND_DIR/lib/mariadb-java-client.jar:$BACKEND_DIR/lib/jbcrypt.jar"

find "$BACKEND_DIR/src" -name "*.java" > /tmp/airroutex_sources.txt
javac -cp "$COMPILE_CP" -d "$WEBAPP_DIR/WEB-INF/classes" @/tmp/airroutex_sources.txt
rm /tmp/airroutex_sources.txt

echo "==> Copying db.properties into WEB-INF/classes..."
cp "$BACKEND_DIR/db.properties" "$WEBAPP_DIR/WEB-INF/classes/db.properties"

echo "==> Copying dependency jars into WEB-INF/lib (mariadb + jbcrypt only, NOT servlet-api)..."
cp "$BACKEND_DIR/lib/mariadb-java-client.jar" "$WEBAPP_DIR/WEB-INF/lib/"
cp "$BACKEND_DIR/lib/jbcrypt.jar" "$WEBAPP_DIR/WEB-INF/lib/"

echo "==> Copying web.xml..."
cp "$BACKEND_DIR/web.xml" "$WEBAPP_DIR/WEB-INF/web.xml"

echo "==> Copying frontend files into webapp root..."
cp -r "$FRONTEND_DIR"/* "$WEBAPP_DIR/"

echo ""
echo "==> Build complete: $WEBAPP_DIR"
echo "    Deploy it with:  cp -r webapp \$CATALINA_HOME/webapps/AirRouteX"
echo "    Then visit:      http://localhost:8080/AirRouteX/"
