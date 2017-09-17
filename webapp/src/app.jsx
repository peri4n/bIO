import React from 'react';
import {render} from 'react-dom';
import FileUpload from './file_upload.jsx';
import SearchBar from './search_bar.jsx';

render(
    (<div style={{width: '500px', marginLeft: 'auto', marginRight: 'auto'}}>
        <p/>
        <SearchBar/>
        <p/>
        <FileUpload/>
    </div>), document.getElementById("app"));
