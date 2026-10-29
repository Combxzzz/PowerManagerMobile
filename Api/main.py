from typing import Literal

from fastapi import FastAPI, HTTPException, BackgroundTasks
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
def shutdown_now(background_task: BackgroundTasks):
    shutdown_manager.cancel()

    background_task.add_task(shutdown_manager.execute_shutdown_now)

    return {
        "message" : "Shutting down now",
        "status" : True
    }
