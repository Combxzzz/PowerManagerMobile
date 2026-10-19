from __future__ import annotations


import json
from http.server import HTTPServer, BaseHTTPRequestHandler

from shutdown import Shutdown


class RequestHandler(BaseHTTPRequestHandler):
    def __init__(self, request, client_address, server: PowerManagerServer):
        super().__init__(request, client_address, server)
        self.shutdown_manager = server.shutdown_manager


    def do_GET(self) -> None:
        if self.path == "/status":
            ResponseUtils.send_OK_status(self, {"status" : "Online"})
            return

        elif self.path == "/shutdown/status":
            ...


    def do_POST(self) -> None:
        if self.path == "/test":
            ResponseUtils.send_OK_status(self, {"message" : "Command received"})
            return

        elif self.path == "/shutdown/schedule":
            content_length = int(self.headers.get("Content-Length", 0))

            post_data = self.rfile.read(content_length)
            body_string = post_data.decode("utf-8")

            try:
                body_json= json.loads(body_string)

                if not isinstance(body_json, dict):
                    ResponseUtils.send_error(self, 400, "Bad request")
                    return

                if "minutes" not in body_json:
                    ResponseUtils.send_error(self, 400, "Param minutes is required")
                    return

                minutes = body_json.get("minutes")

                if type(minutes) is not int:
                    ResponseUtils.send_error(self, 400, "Minute must be a Integer")
                    return

                if minutes not in [15, 30, 60]:
                    ResponseUtils.send_error(self, 400, "Invalid minute")
                    return

                print(f"shutdown scheduled for {minutes} minutes")

            except json.JSONDecodeError:
                ResponseUtils.send_error(self, 400, "Bad request")
                return

            response_data = {
                "message" : "Schedule accepted",
                "minutes" : minutes
            }

            ResponseUtils.send_OK_status(self, response_data)
            return

        else:
            ResponseUtils.send_404_error(self)
            return


class ResponseUtils:
    @staticmethod
    def set_headers(rh: RequestHandler) -> None:
        rh.send_header("Content-Type", "application/json")
        rh.end_headers()
        return

    @staticmethod
    def send_404_error(rh: RequestHandler) -> None:
        rh.send_response(404)
        ResponseUtils.set_headers(rh)
        rh.wfile.write(json.dumps({"error" : "Not found"}).encode("utf-8"))
        return

    @staticmethod
    def send_error(rh: RequestHandler, response_code: int, error: str) -> None:
        rh.send_response(response_code)
        ResponseUtils.set_headers(rh)
        rh.wfile.write(json.dumps({"error" : error}).encode("utf-8"))
        return

    @staticmethod
    def send_OK_status(rh: RequestHandler, content: dict):
        rh.send_response(200)
        ResponseUtils.set_headers(rh)
        rh.wfile.write(json.dumps(content).encode("utf-8"))
        return

class PowerManagerServer(HTTPServer):
    def __init__(self, server_address, RequestHandlerClass, shutdown_manager: Shutdown):
        self.shutdown_manager = shutdown_manager
        super().__init__(server_address, RequestHandlerClass)

def run_server() -> None:
    address = ("0.0.0.0", 8000)

    shutdown_manager = Shutdown()
    server = PowerManagerServer(address, RequestHandler, shutdown_manager) # type: ignore

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("Shutdown")
    finally:
        server.server_close()


if __name__ == "__main__":
    run_server()