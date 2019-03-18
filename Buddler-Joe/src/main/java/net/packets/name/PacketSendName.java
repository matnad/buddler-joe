package net.packets.name;

import net.packets.Packet;

public class PacketSendName extends Packet {
        private String name;
        /**
         * Package to respond to the client that the Login has been successful
         */

        public PacketSendName(String data) {
            super(PacketTypes.SEND_NAME);
            setData(data);
            this.name = data;
            validate();
        }

        public PacketSendName(int clientId, String name){
            super(PacketTypes.SEND_NAME);
            setData(name);
            setClientId(clientId);
            this.name = name;
            validate();
        }

        @Override
        public void validate() {
            if(name != null) {
                isExtendedAscii(name);
                if(name.length()<=9){
                    addError("No name attached to the message.");
                }
            }else{
                addError("No Name found.");
            }
        }

        @Override
        public void processData() {
            try {
                if (name.substring(0,2).startsWith("OK")) {
                    System.out.println("The player searched for is: " + name.substring(3));
                } else {
                    if (hasErrors()) {
                        System.out.println(createErrorMessage());
                    } else {
                        System.out.println(name);
                    }
                }
            } catch (StringIndexOutOfBoundsException e){
                System.out.println(name);
            }
        }
}
