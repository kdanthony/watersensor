/*

Particle Photon Water Sensor

Copyright (c) 2015, Kevin Anthony (kevin@anthonynet.org)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

preferences {
  input("deviceId", "text", title: "Device ID")
  input("token", "text", title: "Access Token")
}

metadata {

  definition (name: "Particle Photon Water Sensor", author: "Kevin Anthony") {
    capability "Water Sensor"
    capability "Refresh"
    capability "Polling"

    attribute "open", "string"
    attribute "closed", "string"
    command "setWaterState"
  }


  tiles(scale: 2) {
    multiAttributeTile(name:"status", type: "generic", width: 6, height: 4){
      tileAttribute ("device.water", key: "PRIMARY_CONTROL") {
        attributeState "wet", label:'${name}', icon:"st.alarm.water.wet", backgroundColor:"#ffa81e"
        attributeState "dry", label:'${name}', icon:"st.alarm.water.dry", backgroundColor:"#79b821"
      }
    }
    standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    main(["status", "water"])
    details(["status", "water", "refresh"])
  }

}

def parse(String description) {
  return null;
}

def poll() { 
  refresh();
}

def refresh() {
  updateWaterState('waterstate');
}

private updateWaterState(command) {
  log.debug("Updating Water State via https://api.spark.io/v1/devices/${deviceId}/waterstate");
  httpPost(
    uri: "https://api.spark.io/v1/devices/${deviceId}/waterstate",
    body: [access_token: token, command: level],
  ) {
    response ->
      log.debug(response.data.return_value)
      if (response.data.return_value == 1) {
        sendEvent(name: 'water', value: 'wet' )
        log.debug("Water detected");
      }
      else {
        sendEvent(name: 'water', value: 'dry' )
        log.debug('Water not detected');
      }
      log.debug (response.data)}
}


private setWaterState(state) {
  log.debug("Setting water state to ${state}");
  sendEvent(name: 'water', value: state )
}
