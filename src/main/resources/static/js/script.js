var websocket = new WebSocket(`ws:${window.location.href.split('http:')[1]}/websocket`);
websocket.onopen = function () {
    console.log("connection success！")
};

websocket.onmessage = function (event) {
    console.log("receive  " + event.data);
    const audio = document.querySelector(`audio${event.data}`),
        key = document.querySelector(`.key${event.data}`);

    if (!key) return;

    const keyNote = key.getAttribute("data-note");
    key.classList.add("playing");
    note.innerHTML = keyNote;
    audio.currentTime = 0;
    audio.play();

};
websocket.onclose = function () {
    console.log("closed websocket!")
};

const keys = document.querySelectorAll(".key"),
    note = document.querySelector(".nowplaying"),
    hints = document.querySelectorAll(".hints");

function playNote(e) {
    const audio = document.querySelector(`audio[data-key="${e.keyCode}"]`),
        key = document.querySelector(`.key[data-key="${e.keyCode}"]`);

    websocket.send(`[data-key="${e.keyCode}"]`);
    console.log("press and send  " + `[data-key="${e.keyCode}"]`);
    if (!key) return;

    const keyNote = key.getAttribute("data-note");

    key.classList.add("playing");
    note.innerHTML = keyNote;
    audio.currentTime = 0;
    audio.play();
}

function removeTransition(e) {
    if (e.propertyName !== "transform") return;
    this.classList.remove("playing");
}

function hintsOn(e, index) {
    e.setAttribute("style", "transition-delay:" + index * 50 + "ms");
}

hints.forEach(hintsOn);

keys.forEach(key => key.addEventListener("transitionend", removeTransition));

window.addEventListener("keydown", playNote);

