echo 0 | sudo tee /sys/class/leds/led0/brightness
echo 0 | sudo tee /sys/class/leds/led1/brightness
cd ~/MagicMirror
DISPLAY=:0 npm start
