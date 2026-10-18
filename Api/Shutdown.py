from datetime import datetime, timedelta, timezone

import math


class Shutdown:
    def __init__(self):
        self.scheduled: bool = False
        self.minutes: int | None = None
        self.deadline: datetime | None = None

    def schedule(self, minutes: int) -> bool:
        if minutes <= 0:
            return False

        self.minutes = minutes
        self.deadline = datetime.now(timezone.utc) + timedelta(minutes=minutes)
        self.scheduled = True
        return True

    def cancel(self) -> bool:
        if not self.scheduled:
            return False

        self.scheduled = False
        self.minutes = None
        self.deadline = None
        return True

    def get_status(self) -> dict:
        return {
            "scheduled" : self.scheduled,
            "minutes" : self.minutes,
            "deadline" : self.deadline.isoformat() if self.deadline else None,
            "remaining" : self.get_remaining_seconds()
        }

    def get_remaining_seconds(self) -> None | int:
        if not self.scheduled or not self.deadline:
            return None

        remaining = (self.deadline - datetime.now(timezone.utc)).total_seconds()

        return math.ceil(max(0, remaining))
