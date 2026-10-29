from datetime import datetime, timedelta, timezone
import math
import subprocess
from time import sleep


class Shutdown:
    def __init__(self):
        self.scheduled: bool = False
        self.minutes: int | None = None
        self.deadline: datetime | None = None

    def schedule(self, minutes: int) -> bool:
        if minutes <= 0:
            return False

        try:
            subprocess.run(["shutdown", f"+{minutes}"], check=True)
            self.minutes = minutes
            self.deadline = (
                datetime.now(timezone.utc)
                + timedelta(minutes=minutes)
            )
            self.scheduled = True
            return True
        except (subprocess.CalledProcessError, OSError):
            return False

    def cancel(self) -> bool:
        if not self.scheduled:
            return False

        try:
            subprocess.run(["shutdown", "-c"], check=True)
            self.scheduled = False
            self.minutes = None
            self.deadline = None
            return True
        except (subprocess.CalledProcessError, OSError):
            return False


    def execute_shutdown_now(self) -> None:
        try:
            import time
            time.sleep(1)
            subprocess.run(["shutdown", "now"], check=True)
        except Exception as e:
            print(f"Error during shutdown: {e}")


    def get_status(self) -> dict:
        return {
            "scheduled": self.scheduled,
            "minutes": self.minutes,
            "deadline": (
                self.deadline.isoformat()
                if self.deadline
                else None
            ),
            "remaining": self.get_remaining_seconds(),
        }

    def get_remaining_seconds(self) -> None | int:
        if not self.scheduled or not self.deadline:
            return None

        remaining = (
                self.deadline - datetime.now(timezone.utc)
        ).total_seconds()

        if remaining <= 0:
            self.scheduled = False

        return math.ceil(max(0, remaining))
