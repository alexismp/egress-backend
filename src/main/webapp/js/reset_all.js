/*
Copyright 2015 Google Inc. All rights reserved.
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

// Get a reference to the data set
var ref = new Firebase("https://shining-inferno-9452.firebaseio.com/stations");

// Create a callback to handle the result of the authentication
function authHandler(error, authData) {
    if (error) {
        console.log("Login Failed!", error);
    } else {
        console.log("Authenticated successfully with payload:", authData);
        console.log("Name: ", authData.google.displayName);
        updateUserUI(authData.google.displayName);

        resetAll();
        updateUserUI("Done reseting all station owners!");
    }
}

// brute force FTW!
function resetAll() {
    for (var i = 1; i <= 6441; i++) {
        var station = ref.child(i);
        station.update({
            "owner": "",
            "when": "",
            "OwnerMail":""
        });
    }
}

function updateUserUI(message) {
    var legend = document.getElementById('userlegend');
    var div = document.createElement('div');
    div.innerHTML = "User: " + message;
    legend.appendChild(div);
}

ref.authWithOAuthPopup("google", authHandler);

