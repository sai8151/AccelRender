import bpy
import requests
import threading
import time
server_url = "http://192.168.81.8:8080"
object_name = "Cube"
def update_object_position(x, y, z):
    obj = bpy.data.objects.get(object_name)
    if obj:
        obj.location = (x, y, z)

def main():
    iterations = 2000

    for _ in range(iterations):
        try:
            response = requests.get(server_url)
            response_text = response.text

            # Extract X, Y, and Z values from response
            x_start = response_text.find("X=") + 2
            y_start = response_text.find("Y=") + 2
            z_start = response_text.find("Z=") + 2

            x_end = response_text.find(" ", x_start)
            y_end = response_text.find(" ", y_start)
            z_end = response_text.find(" ", z_start)

            x_value = float(response_text[x_start:x_end])
            y_value = float(response_text[y_start:y_end])
            z_value = float(response_text[z_start:z_end])

            update_object_position(x_value, y_value, z_value)

        except Exception as e:
            print("Error:", e)

def run_in_thread():
    thread = threading.Thread(target=main)
    thread.start()

if __name__ == "__main__":
    run_in_thread()
