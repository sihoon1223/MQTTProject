function dataTipFunc(seriesId, seriesName, index, xName, yName, data, values) {
   return "<table cellpadding='0' cellspacing='1'>"
      +"<tr>"
        +"<td>current connections(개수)</td><td align='center'>" + values[1] + "</td>"
       +"</tr><tr>"
         +"<td>recent client</td><td align='center'>" + data.recent + "</td>"
        +"</tr><tr>"
      +"<td>old client</td><td align='center'>" + data.old + "</td>"
       +"</tr><tr>"
         +"<td>minimum msg</td><td align='center'>" + data.minimum + "</td>"
        +"</tr><tr>"
         +"<td>maximum msg</td><td align='center'>" + data.maximum + "</td>"
      +"</tr><tr>"
     +"</tr></table>";
}

function rMateChartH5ChangeTheme(theme){
  window.rMateChartH5.registerTheme(window.rMateChartH5.themes);
  document.getElementById("receivingPie").setTheme(theme);
}