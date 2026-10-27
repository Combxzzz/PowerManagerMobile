from typing import Literal

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

from shutdown import Shutdown

app = FastAPI(
    title="Power Manager",
    description="API for managing PC power operations",
    version="1.0.0",
)

shutdown_manager = Shutdown()


class ShutdownScheduleRequest(BaseModel):
    minutes: Literal[15, 30, 60]


@app.get("/status")
def get_status():
    return {"status" : "Online"}

@app.get("/shutdown/status")
def get_shutdown_status():
    return shutdown_manager.get_status()

@app.post("/test")
def test():
    return {"message" : "Command received"}

@app.post("/shutdown/schedule")
def schedule_shutdown(request: ShutdownScheduleRequest):
    status = shutdown_manager.schedule(request.minutes)

    if not status:
        raise HTTPException(
            status_code=409,
            detail="Could not schedule shutdown"
        )

    return {
        "message" : "Schedule accepted",
        "status" : status,
        "minutes" : request.minutes
    }

@app.post("/shutdown/cancel")
def cancel_shutdown():
    cancelled = shutdown_manager.cancel()

    return {
        "cancelled" : cancelled,
        "message" : ("Schedule cancelled" if cancelled else "No active schedule")
    }

@app.post("/shutdown/now")
def shutdown_now():
    status = shutdown_manager.shutdown_now()

    if not status:
        raise HTTPException(
            status_code=500,
            detail="Could not execute immediate shutdown"
        )

    return {
        "message" : "Shutting down now",
        "status" : True
    }
