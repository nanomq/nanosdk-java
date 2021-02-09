#!/bin/sh

if [ ! -d "$1" ]; then
	echo "provide a valid deployment base path!"
	exit 1
fi

VERSION="$(./gradlew -q getVersion)"
echo "Found version ${VERSION}"

if ! ./gradlew -q javadoc; then
	echo "failed to generate javadoc!"
	exit 1
fi

SOURCE="$(realpath ./build/docs/javadoc)"
DEST="$1/nng-java/${VERSION}"

if ! mkdir -p "${DEST}"; then
	echo "failed to make destination directory '${DEST}'"
	exit 1
fi

echo "Copying docs from '${SOURCE}' to '${DEST}'"
if ! cp -Rf "${SOURCE}/" "${DEST}/"; then
	echo "failed copying files!"
	exit 1
fi

