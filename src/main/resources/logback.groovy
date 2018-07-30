package togit

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[2git][%level] %d{HH:mm:ss} - %msg%n"
    }
}

appender("LOGFILE", FileAppender) {
    def jenkinsBuildNumber = System.getenv("BUILD_NUMBER")
    def timestamp = new Date().format("dd-MM-yyyy_HH-mm-ss")
    def logDir = System.getenv('LOG_DIR') ?: '.'

    file = "$logDir/2git-${jenkinsBuildNumber ?: timestamp}.log"

    encoder(PatternLayoutEncoder) {
        pattern = "[2git][%level] %d{dd/MM/yyyy - HH:mm:ss} - %msg%n"
    }
}

//Get the log level from environment variable LOG_LEVEL
def level = Level.valueOf(System.getenv('LOG_LEVEL') ?: 'INFO')
root(level, ["STDOUT", "LOGFILE"])
