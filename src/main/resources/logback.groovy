package toGit

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[2git][%level] %d{HH:mm:ss} - %msg%n"
    }
}

appender("DEBUG", FileAppender) {
    def stamp = System.getenv("BUILD_NUMBER") ?: new Date().format("dd-MM-yyyy_HH-mm-ss")
    file = "2git-${stamp}.log"
    encoder(PatternLayoutEncoder) {
        pattern = "[2git][%level] %d{dd/MM/yyyy - HH:mm:ss} - %msg%n"
    }
}

//Get the log level from environment variable LOG_LEVEL
def env = System.getenv()
def level = Level.valueOf(Optional.ofNullable(env['LOG_LEVEL']).orElse("INFO"))

root(level, ["STDOUT", "DEBUG"])