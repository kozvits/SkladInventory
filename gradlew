#!/bin/sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java version to use.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME. Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1
    if [ $? -eq 0 ] ; then
        JAVACMD=`which java`
        JAVACMD_IS_ADMIN=`$JAVACMD -version 2>&1 | grep -i "server"`
    else
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    fi
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" ] && [ "$darwin" = "false" ] && [ "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" ] || [ "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -eq 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if [ "$cygwin" = "true" ] || [ "$msys" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
    ROOTDIRS=`for dir in $ROOTDIRSRAW; do echo $dir; done | sed -e 's|/||g'`
    if [ -n "$ROOTDIRS" ] ; then
        sep='|'
        for dir in $ROOTDIRS; do
            PATTERN="$PATTERN$sep$dir"
            sep='|'
        done
        PATTERN="^($PATTERN)/"
    fi
    if [[ "$JAVACMD" =~ $PATTERN ]] ; then
        JAVACMD=`cygpath --path --ignore --mixed "$JAVACMD"`
    fi
    if [[ "$CLASSPATH" =~ $PATTERN ]] ; then
        CLASSPATH=`cygpath --path --ignore --mixed "$CLASSPATH"`
    fi
fi

# Split up the JVM options from the others. We need to be careful to not split on spaces that are part of an option.
split_jvm_opts () {
    JVM_OPTS=""
    while [ "$#" -gt 0 ] ; do
        case "$1" in
            -D*)
                JVM_OPTS="$JVM_OPTS $1"
                shift
                ;;
            *)
                REST="$1"
                shift
                break
                ;;
        esac
    done
    echo "$JVM_OPTS"
    while [ "$#" -gt 0 ] ; do
        REST="$REST $1"
        shift
    done
    echo "$REST"
}

eval set -- `split_jvm_opts $DEFAULT_JVM_OPTS $GRADLE_OPTS "$@"`
JVM_OPTS=$1
shift
REST=$@

exec "$JAVACMD" \
    $JVM_OPTS \
    -classpath "$CLASSPATH" \
    -Dorg.gradle.appname=$APP_BASE_NAME \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
