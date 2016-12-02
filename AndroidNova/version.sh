#/bin/bash

echo "Auto Version: `pwd`"

#VERSION=`git describe --tag --dirty`
VERSION='NovaPlanner v1.0.1';

echo "   Ver:  ${VERSION}"

cat AndroidManifest.xml | \
    sed -e "s/android:versionName=\".*\"/android:versionName=\"${VERSION}\"/" \
    > bin/AndroidManifest.xml

exit 0
