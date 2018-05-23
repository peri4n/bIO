import React from 'react';

export default class SearchBar extends React.Component {

    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit(event) {
        const oReq = new XMLHttpRequest();
        oReq.open('GET', 'http://localhost:9000/index/search?sequence=' + this.state.value, true);
        oReq.setRequestHeader('Content-Type', 'text/plain');

        oReq.onload = () => {
            document.getElementById('result').innerHTML = oReq.responseText
        };

        oReq.send();

        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <label htmlFor="search-bar">
                    Search bar:
                    <input type="text" name="search-term" id="search-bar" value={this.state.value} onChange={this.handleChange}/>
                </label>
                <input type="submit" value="Submit" />
                <div id="result"/>
            </form>
        );
    }

}
