import React from 'react';
import { render } from 'react-dom';

class FileUpload extends React.Component {
    render() {
        return (
            <div>
                <input type="file" onChange={event => this.logFile(event)} action="javascript:void(0);" />
                <div id="ids" />
            </div>
        );
    }

    logFile(event) {
        const input = event.target;
        const reader = new FileReader();

        reader.onload = function () {
            const oReq = new XMLHttpRequest();
            oReq.open("POST", "http://localhost:9000/index/add", true);
            oReq.onload = () => {
                document.getElementById("ids").innerHTML = JSON.parse(oReq.responseText).join(" ")
            };
            oReq.send(reader.result);
        };
        reader.readAsBinaryString(input.files[0]);
    }
}

render(<FileUpload />, document.getElementById("app"));
