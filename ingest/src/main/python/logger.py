#  Copyright 2025 Patrick Hoette
#
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the “Software”), to deal in the Software without restriction, including without limitation
#  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
#  to permit persons to whom the Software is furnished to do so.
#
#  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
#  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
#  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
#  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
#  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
#  OTHER DEALINGS IN THE SOFTWARE.

#
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the “Software”), to deal in the Software without restriction, including without limitation
#  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
#  to permit persons to whom the Software is furnished to do so.
#
import sys
import logging

# Constants

_USE_COLOR = sys.stdout.isatty()

_BLUE = '\033[1;34m'
_YELLOW = '\033[1;33m'
_RED = '\033[1;31m'
_RESET = '\033[0m'

_LEVEL_COLORS = {
    logging.DEBUG   : None,
    logging.INFO    : _BLUE,
    logging.WARNING : _YELLOW,
    logging.ERROR   : _RED,
    logging.CRITICAL: _RED,
}


# Classes

class ColoredFormatter(logging.Formatter):
    def format(self, record):
        color = _LEVEL_COLORS.get(record.levelno, None)
        message = super().format(record)

        if color and _USE_COLOR:
            return f'{color}{message}{_RESET}'

        return message


# Functions

def _get_logger(name: str = None, level: int = logging.DEBUG) -> logging.Logger:
    logger = logging.getLogger(name)

    if not logger.handlers:
        handler = logging.StreamHandler()
        formatter = ColoredFormatter(fmt = '%(asctime)s [%(levelname)s] %(message)s', datefmt = '%H:%M:%S')
        handler.setFormatter(formatter)
        logger.addHandler(handler)
        logger.setLevel(level)

    return logger


_default_logger = _get_logger()


def debug(message):
    _default_logger.debug(message)


def info(message):
    _default_logger.info(message)


def warning(message):
    _default_logger.warning(message)


def error(message):
    _default_logger.error(message)


def critical(message):
    _default_logger.critical(message)
