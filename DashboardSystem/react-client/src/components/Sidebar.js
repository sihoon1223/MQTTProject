import React from 'react';
import { NavLink } from 'react-router-dom';
import { Navbar, Nav } from 'react-bootstrap';
import duck from '../images/duck.png';
import circle from '../images/circle.png';
import circle2 from '../images/circle3.png';
import circle3 from '../images/circle4.png';

// sidebar

const Sidebar = () => {
  const navbarStyle = {
    backgroundColor: 'rgb(52, 69, 85)',
    position: 'fixed',
    top: '0',
    bottom: '0',
    left: '0',
    zIndex: '10', /* Behind the navbar */
    padding: '48px 0 0', /* Height of navbar */
    height: 'calc(100vh)',
    boxShadow: 'inset -1px 0 0 rgba(0, 0, 0, .1)',
    paddingTop: '.5rem',
    overflowX: 'hidden',
    overflowY: 'auto',
  };

  const brandStyle = {
    backgroundColor: 'rgb(52, 69, 85)',
    
  };

  const navlinkStyle = {
    backgroundColor: 'rgb(52, 69, 85)',
    
  }

  const imgStyle = {
    width:"35px",
    height:"35px",
    backgroundColor: 'rgb(52, 69, 85)',
    marginLeft:'-2px',
    marginBottom:'20px'
  }

  const circleStyle = {
    width:"15px",
    height:"15px",
    marginLeft:"8px",
    marginBottom:'20px',
    backgroundColor: 'rgb(52, 69, 85)',
    
  }

  const selectedStyle = {
    
  }

  return (
    <Navbar style={navbarStyle} collapseOnSelect expand="lg">
      {/*<Navbar.Collapse id="responsive-navbar-nav">*/}
        <Nav>
            <ul style={{listStyle:'none', margin:'3px', paddingLeft:'0px', marginTop:'-50vh'}}>
                <li>
                <NavLink className="nav-link" style={brandStyle} exact to="/"> {/*로고 클릭 시 메인 화면으로*/}
                    <img alt="오리" src={duck} style={imgStyle} className="d-inline-block align-top"/>
                    {/*{' 황금오리'}*/}
                </NavLink>
                </li>
                <li>
                <NavLink style={navlinkStyle} activeStyle={selectedStyle} className="nav-link" exact to="/" >
                    <img alt="" src={circle} style={circleStyle} className="d-inline-block align-top"/>
                </NavLink>
                </li>
                <li>
                <NavLink style={navlinkStyle} activeStyle={selectedStyle} className="nav-link" to="/traffic" >
                    <img alt="" src={circle2} style={circleStyle} className="d-inline-block align-top"/>
                </NavLink>
                </li>
                <li>
                <NavLink style={navlinkStyle} activeStyle={selectedStyle} className="nav-link" to="/subscription" >
                    <img alt="" src={circle3} style={circleStyle} className="d-inline-block align-top"/>
                </NavLink>
                </li>
            </ul>
        </Nav>
      {/*</Navbar.Collapse>*/}
    </Navbar>
  );
};

export default Sidebar;
