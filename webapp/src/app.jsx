import React from 'react';
import { render } from 'react-dom';

render((
    <div>
        <form>
            <label for="search-bar">
              Search bar:
            </label>
            <input type="text" name="search-term" id="search-bar"/>
        </form>
    </div>), document.getElementById("app"));
