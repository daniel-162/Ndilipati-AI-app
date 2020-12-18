# Ndilipati-AI-app
The goal of Ndilipati is to aid those with visual impairment to better interact and navigate their environment.



# Build the demo using Android Studio
Application can run either on device or emulator.

## For GammaNdilipati
The app uses tflite models which are build using gradle scripts and no need to download from somewhere else.
Custom object detection tflite model can be trained and put in the assets folder with it's labelmap.
### requirements
1. android studio
2. android device and android environment with minimum API level 21.
### running demo
1. clone this repo: https://github.com/daniel-162/Ndilipati-AI-app.git
2. open android studio and open GammaNdilipati as project
3. Run the app from android studio once your android device is connected 

## For BetaNdilipati
The app uses yolo tiny models which are build by copying dnns folder to your android device.
New yolo-v3 model and cfg can be placed in the dnns folder for custom object detection.
In future the app will automatically download these files from the cloud at first launch.
### requirements
1. android studio
2. android device and android environment with minimum API level 28.
### running demo
1. clone this repo: https://github.com/daniel-162/Ndilipati-AI-app.git
2. open android studio and open BetaNdilipati as project
3. take the dnns folder and copy it to your phone.
3. Run the app from android studio once your android device is connected 
