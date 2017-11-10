#Project description
1This project deals with creating a smart family board. As the 
project evolves, this document will reflect that. 

#Capabilities
The smart board launches Chromium in kiosk mode and displays dakboard.com. It 
is controllable by Alexa and a motion sensor. Alexa can turn the display on and 
off as well as replacing the photos for display. The motion sensor allows the
smart board to turn off to save energy; it will enable the smartboard when 
it senses motion.

#Hardware
Raspberry Pi zero
Monitor (used old laptop screen, and an LCD controller board)

#Software
Everything will be written in Java (I'm a Java developer by day) 
The Raspberry Pi will run a Java app that connects to Amazon AWS IoT. It will 
receive control messages from the IoT endpoint.
An Alexa skill (backed by AWS Lambda function) will be a Java app that responds
to user input from Alexa.
