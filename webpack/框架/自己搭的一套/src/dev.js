import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Welcome from './containers/welcome/index.jsx'

ReactDOM.render(
  <BrowserRouter>
    <Routes>
        <Route path='/' element={<Welcome />} />
    </Routes>
  </BrowserRouter>,
  document.getElementById('root')
);