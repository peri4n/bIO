import React from 'react';
import { render } from 'react-dom';

class FileUpload extends React.Component {
    render() {
        return (
            <input type="file" onChange={event => this.logFile(event)}/>
        );
    }

    logFile(event) {
        const input = event.target;
        const reader = new FileReader();

        reader.onload = function () {
            console.log(reader.result)
            const oReq = new XMLHttpRequest();
            oReq.open("POST", "http://localhost:9000/", true);
            oReq.onload = () => {
                console.log(oReq.response);
            };
            oReq.send(reader.result);
        };
        reader.readAsBinaryString(input.files[0]);
    }
}

render(<FileUpload />, document.getElementById("app"));
