// -----------------------------------------------------------------------------
// * * *  DWcommon - all DWAXE common JavaScript functions  * * *
//
// Copyright Ericsson AB 2011-2013. All rights reserved.
//
// This file is included in and used in ALEX, DHS and X2HTML output formats.
// Function ht() contains free software from sixrevisions.com
//
//
// HISTORY
// -------
// 2011-10-12  xantsib   Review support added, show next/prev changes (R32).
// 2012-02-09  xantsib   Fix for both navigation buttons sometimes inactive.
// 2013-03-14  Saikumar  Functions for tooltip and collapsible (+/-) TOC added.
// 2013-11-27  XALOPEP Added new functions for Doclist filtering.
// 2015-01-07  Suresh    Changes for Document Content Filtering
// 2015-03-12  Suresh    Changes in getCookie to read the cookie value till ;
// -----------------------------------------------------------------------------

var footer1;
var ttO;
function fadein()
{
  footer1=document.getElementById('footer');
  if(footer1){
  footer1.style.opacity=1;
  footer1.style.filter = 'alpha(opacity=100)';}
}
function fadeout()
{
  footer1=document.getElementById('footer');
  if(footer1){
  footer1.style.opacity=0.1;
  footer1.style.filter = 'alpha(opacity=10)';}
}
function footerfun(){
  var hasVScroll = document.documentElement.scrollHeight > document.documentElement.clientHeight;
  if(!hasVScroll)
  {
    footer1=document.getElementById('footer');
    if(footer1)
      footer1.style.display='none';
  }else{
    setTimeout('fadeout()', 4900);
  }
}

setTimeout('footerfun()', 100);

function getCount(countVal)
{
  c_hits = countVal;
}
var c_count = 0;
var c_curr = "";
var c_id = "c_1";

function showNextChange() {
  if(c_count != c_hits)
  document.getElementById(c_id).style["backgroundColor"] = "";
  if(c_count < (c_hits - 1)) {
    c_id = "c_" + ++c_count;
    document.getElementById(c_id).style["backgroundColor"] = "#ffffbf";
    document.getElementById(c_id).scrollIntoView(true);
    document.getElementById("c_prev_but").style.visibility = "visible";
    document.getElementById("c_prev_but").style.opacity = 1;
    document.getElementById("c_prev_but").style.filter = 'alpha(opacity=100)';
    document.getElementById('c_prev_but').disabled = false;
  }
  else if(c_count == (c_hits - 1)) {
      c_id = "c_" + ++c_count;
    document.getElementById(c_id).style["backgroundColor"] = "#ffffbf";
    document.getElementById(c_id).scrollIntoView(true);
    document.getElementById("c_next_but").style.visibility = "visible";
    document.getElementById("c_next_but").style.opacity = 0.2;
    document.getElementById("c_next_but").style.filter = 'alpha(opacity=20)';
    document.getElementById('c_next_but').disabled = true;
    document.getElementById("c_prev_but").style.visibility = "visible";
    document.getElementById("c_prev_but").style.opacity = 1;
    document.getElementById("c_prev_but").style.filter = 'alpha(opacity=100)';
    document.getElementById('c_prev_but').disabled = false;
  }
  else {
    c_count = c_hits;
    document.getElementById("c_next_but").style.visibility = "visible";
    document.getElementById("c_next_but").style.opacity = 0.2;
    document.getElementById("c_next_but").style.filter = 'alpha(opacity=20)';
    document.getElementById('c_next_but').disabled = true;
    document.getElementById("c_prev_but").style.visibility = "visible";
    document.getElementById("c_prev_but").style.opacity = 1;
    document.getElementById("c_prev_but").style.filter = 'alpha(opacity=100)';
    document.getElementById('c_prev_but').disabled = false;
  }
  if(c_count > c_hits)
    c_curr = c_hits;
  else
    c_curr = c_count + "/" + c_hits;
  document.getElementById("c_prev_but").title = "Show previous change  |  " + c_curr;
  document.getElementById("c_next_but").title = "Show next change  |  " + c_curr;
}

function showPrevChange() {
  document.getElementById(c_id).style["backgroundColor"] = "";
  if(c_count > 2) {
    c_id = "c_" + --c_count;
    document.getElementById(c_id).style["backgroundColor"] = "#ffffbf";
    document.getElementById(c_id).scrollIntoView(true);
    document.getElementById("c_next_but").style.visibility = "visible";
    document.getElementById("c_next_but").style.opacity = 1;
    document.getElementById("c_next_but").style.filter = 'alpha(opacity=100)';
    document.getElementById('c_next_but').disabled = false;
  } else if(c_count == 2) {
    c_id = "c_" + --c_count;
    document.getElementById(c_id).style["backgroundColor"] = "#ffffbf";
    document.getElementById(c_id).scrollIntoView(true);
    document.getElementById("c_prev_but").style.visibility = "visible";
    document.getElementById("c_prev_but").style.opacity = 0.2;
    document.getElementById("c_prev_but").style.filter = 'alpha(opacity=20)';
    document.getElementById('c_prev_but').disabled = true;
    document.getElementById("c_next_but").style.visibility = "visible";
    document.getElementById("c_next_but").style.opacity = 1;
    document.getElementById("c_next_but").style.filter = 'alpha(opacity=100)';
    document.getElementById('c_next_but').disabled = false;
  } else {
    c_count = 0;
    document.getElementById("c_prev_but").style.visibility = "visible";
    document.getElementById("c_prev_but").style.opacity = 0.2;
    document.getElementById("c_prev_but").style.filter = 'alpha(opacity=20)';
    document.getElementById('c_prev_but').disabled = true;
    document.getElementById("c_next_but").style.visibility = "visible";
    document.getElementById("c_next_but").style.opacity = 1;
    document.getElementById("c_next_but").style.filter = 'alpha(opacity=100)';
    document.getElementById('c_next_but').disabled = false;
  }
  if(c_count < 1)
    c_curr = c_hits;
  else
    c_curr = c_count + "/" + c_hits;
  document.getElementById("c_prev_but").title = "Show previous change  |  " + c_curr;
  document.getElementById("c_next_but").title = "Show next change  |  " + c_curr;
}

function hideChange(prlcpy) {
  location.href = prlcpy;
}
function showChange(prlcpy) {
  location.href = prlcpy;
}
var show_toc = true;
var FoundMatch = -1;
var isM = 0;
var ua = navigator.userAgent;
if((ua.indexOf(' Mobile') > 0) || (ua.indexOf(' IEMobile') > 0) || (ua.indexOf(' Opera Mobi') > 0) || (ua.indexOf(' Android') > 0))
{
   isM = 1;
}
function show_hide_TOC() {
var TOC=document.getElementById("TOC");
  if (show_toc) {
    TOC.style.display="none";
    document.getElementById("ce_img1").style.display = "none";
    document.getElementById("ce_img2").style.display = "inline";
    show_toc = false;
  } else {
    TOC.style.display="inline";
    document.getElementById("ce_img1").style.display = "inline";
    document.getElementById("ce_img2").style.display = "none";
    show_toc = true;
  }
}

function show_hide_cont(d_id, i_id1, i_id2, fl) {
var TOC=document.getElementById(d_id);
  if (fl) {

    TOC.style.display="none";
    document.getElementById(i_id1).style.display = "none";
    document.getElementById(i_id2).style.display = "inline";
    fl = false;
  } else {

    TOC.style.display="inline";
    document.getElementById(i_id1).style.display = "inline";
    document.getElementById(i_id2).style.display = "none";
    fl = true;
  }
  return fl;
}

var edw = 0;
function edw_path(){
  var dwjs_path = document.getElementsByTagName("script");
  for(i=0;i<dwjs_path.length;i++){
    if(dwjs_path[i].src){
      var index = (dwjs_path[i].src).indexOf("dwcommon.js");
      if(index != -1)
      {
        edw = (dwjs_path[i].src).substring(0,index);
        break;
      }
    }
  }
}

function init(tbl_fol, tbl_doc)
{
  document.getElementById('head2').style.display = "none";
  document.getElementById('main').style.display = "none";
  document.getElementById('hr1').style.display = "none";
  if(isM)
  {
    ChangetoMobileview(tbl_doc, tbl_fol);
    if(!edw)
      edw_path();
    document.getElementById('stylesheet').href=edw+'mob_cvl1.css';
  }
  if(FoundMatch == 0)
  {
     document.write("<div id='nm'>This folder contains no documents matching the current filter selections.</div>");
  }
}

function ChangetoMobileview(tbl_doc, tbl_fol) {
  document.getElementById('head1').style.display = "none";
  document.getElementById('head2').style.display = "block";
  document.getElementById('main').style.display = "block";
  document.getElementById('hr1').style.display = "block";
  var t_id=document.getElementById(tbl_doc);
  if(t_id){
    t_id.cellSpacing="3";
    t_id.cellPadding="5";
    var no_rows=t_id.rows.length;
    var no_clmns;
    var rindex=0;
    while( rindex < no_rows ) {
      no_clmns =t_id.rows[rindex].cells.length;
      while( no_clmns>2 ) {
        no_clmns=no_clmns-1;
        t_id.rows[rindex].deleteCell(no_clmns);
      }
      rindex=rindex+1;
    }
  }
  t_id=document.getElementById(tbl_fol);
  if(t_id){
    t_id.cellSpacing="3";
    t_id.cellPadding="5";
    var no_rows=t_id.rows.length;
    var rw_no=0, rw_id='';
    if(!edw)
       edw_path();
    for(rw_no=1; rw_no<=no_rows; rw_no++){
      rw_id="fi"+rw_no;
      var obj=document.getElementById(rw_id);
      if(obj)
        obj.src=edw+"FC.png";
    }
  }
}

function on_click(info){
    var Str = String(info);
    var array = Str.split("::");
  if(isM==1){
    ht.sh(array[0],array[1],array[2],array[3]);}
  else{
    if(array[2]) {location.href=array[2]; } }
}
function on_mouseover(info,eve){
  var eeU = eve.clientY;
  var eeL = eve.clientX;
  var Str = String(info);
  var array = Str.split("::");
  if(isM==0){
      ttO = setTimeout(function(){ht.sh(array[0],array[1],array[2],array[3],eeU,eeL);},500); 
    }

}
function on_mouseout(){
  if(ttO){clearTimeout(ttO);}
  if(isM==0){ht.hd(); }
}

/*
 Function ht() contains the javascript from sixrevisions.com tutorial which is a free software.
 This javascript has been customized as per our requirements.
*/
var ht=function(){
var id = 'ht';
var speed = 100;
var timer = 100;
var endalpha = 100;
var alpha = 0;
var Delay = 500;
var count = 0;
var tt,t,c,h;
var div_width;
var maxW=0,minW=0;
var winH,winW;
var minL = 15;
var minT = 15;
var ie;
var browserName;
var iteration = 0;
var isOverlay = 0;

return{
sh:function(ht_v,head,link,w,peU,peL){
  ht_v.replace(/&lt;/g,"<").replace(/&gt;/g,">");
if(count == 0){
  browserName = navigator.appName;
  ie = document.all ? true : false;
}
count = count+1;
iteration = 0;
isOverlay = 0;
if(tt == null){
  tt = document.createElement('div');
  tt.setAttribute('id',id);
  t = document.createElement('div');
  t.setAttribute('id',id + 'top');
  c = document.createElement('div');
  c.setAttribute('id',id + 'cont');
  tt.appendChild(t);
  tt.appendChild(c);
  document.body.appendChild(tt);
  tt.style.opacity = 0;
}
winW = ie ? document.documentElement.offsetWidth : window.innerWidth;
maxW = parseInt(winW*(.9));
if(!edw)
  edw_path();
t.innerHTML = head ?
               (link ?
                 (edw ?
                    "<a href=" + link + " class='hd' style='white-space:nowrap'>" + head + "&nbsp; <img src=\""+edw+"XL.gif\" id=\"link_img\"/></a>"
                    : "<a href=" + link + " class='hd'>" + head + "</a>")
                 : head )
               : "";
if(isM==1){
  httop.style.minHeight="30px";
  t.innerHTML = edw ? t.innerHTML + "<img src=\""+edw+"TC.gif\" id=\"htclose\"/>" : t.innerHTML;
}
//ht.pause(650);
if(isM==1) { document.onclick = this.pos; }
else { document.onmousemove = this.pos; }
tt.style.display = 'block';tt.style.top = "-1000px";tt.style.left="-1000px";
c.innerHTML = (isM == 1) ? ht_v : "<span onclick=\"ht.hide();\" style=\"display:block;\">"+ht_v+"</span>";
tt.style.width = w ? w + 'px' : 'auto';
if(!w && ie){
  t.style.display = 'none';
  tt.style.width = tt.offsetWidth;
  t.style.display = 'block';
}
if(head){ minW = (head.length * ((isM==1)?30:15)) + 44; }
if(head &&(tt.offsetWidth < minW)) { tt.style.width = minW; }
if(tt.offsetWidth > maxW){ tt.style.width = maxW + 'px'; }
div_width = tt.offsetWidth;
h = parseInt(tt.offsetHeight) + 1;
if(peU && peL){
  ht.posi(peU,peL);//fix for hover without mouse move
}
clearInterval(tt.timer);
tt.timer = setInterval(function(){ht.fade(1)},timer);
},
pos:function(e){
if(iteration > 0 && isM ==1){ ht.hd(); }
var u = ie ? event.clientY : e.pageY - window.pageYOffset;
var l = ie ? event.clientX : e.pageX - window.pageXOffset;
var orig_u = u;
var orig_l = l;
winH = ie ? document.documentElement.offsetHeight : window.innerHeight;
u = u+15;
if(winH < (u+h+5)) { u = (u-h-20);}
if(u<minT) { u=minT;document.onmousemove = null; }
l = l - (div_width);
if( ((orig_u >= u) && (orig_u <= u+h)) && ((orig_l >= l) && (orig_l<=(l+div_width))) && (isM !=1) )
{
  isOverlay = 1;
}
if(l<=minL) { l = minL; }
if(document.body.scrollLeft)
  l = l+document.body.scrollLeft;
else if(window.pageXOffset)
  l = l+window.pageXOffset;
else if(document.documentElement.scrollLeft)
  l = l+document.documentElement.scrollLeft;
if(document.body.scrollTop)
  u = u+document.body.scrollTop;
else if(window.pageYOffset)
  u = u+window.pageYOffset;
else if(document.documentElement.scrollTop)
  u = u+document.documentElement.scrollTop;
if(iteration === 0 || iteration % 5 === 0){ 
  if (tt){
tt.style.top = (u)+ 'px';
tt.style.left = (l - 1) + 'px';
  }
}
iteration = iteration+1;
return;
},
posi:function(u,l){
  var orig_u = u;
  var orig_l = l;
  winH = ie ? document.documentElement.offsetHeight : window.innerHeight;
  u = u+15;
  if(winH < (u+h+5)) { u = (u-h-20);}
  if(u<minT) { u=minT;document.onmousemove = null; }
  l = l - (div_width);
  if( ((orig_u >= u) && (orig_u <= u+h)) && ((orig_l >= l) && (orig_l<=(l+div_width))) && (isM !=1) ){
    isOverlay = 1;
  }
  if(l<=minL) { l = minL; }
  if(document.body.scrollLeft)
    l = l+document.body.scrollLeft;
  else if(window.pageXOffset)
    l = l+window.pageXOffset;
  else if(document.documentElement.scrollLeft)
    l = l+document.documentElement.scrollLeft;
  if(document.body.scrollTop)
    u = u+document.body.scrollTop;
  else if(window.pageYOffset)
    u = u+window.pageYOffset;
  else if(document.documentElement.scrollTop)
    u = u+document.documentElement.scrollTop;
  tt.style.top = (u)+ 'px';
  tt.style.left = (l - 1) + 'px';
  return;
},
fade:function(d){
var a = alpha;
if((a != endalpha && d == 1) || (a != 0 && d == -1)){
  var i = speed;
  if(endalpha - a < speed && d == 1){ i = endalpha - a; }
  else if(alpha < speed && d == -1){  i = a; }
  alpha = a + (i * d);
  tt.style.opacity = alpha * .01;
}
else{
  clearInterval(tt.timer);
  if(d == -1){tt.style.display = 'none';tt=null;}
}
},
pause :function(millis){
var date = new Date();
var curDate = null;
do { curDate = new Date(); }
  while(curDate-date < millis);
},
hd:function(){
if( isM !=1 ){
if( (isOverlay != 1) ){
  if(tt && tt.timer){
    clearInterval(tt.timer);
    tt.timer = setInterval(function(){ht.fade(-1);isOverlay = 0;},1);
  }
}
}
else
{
  clearInterval(tt.timer);
  tt.timer = setInterval(function(){ht.fade(-1)},timer);
}
},
hide:function(){
  isOverlay = 0;
  ht.hd();
}
};
}();
/*
XALOPEP Filter logic for doclist for categorization 27 Nov 2013
Added filterDoclist, filterVisibility, and getCookie.
*/
function filterDoclist(tableID,defaultMap,cookieName) {
    var CookieArray;
    var CookieValue = getCookie(cookieName);
    tableID = document.getElementById(tableID);
    CookieValue = CookieValue ? CookieValue : defaultMap;
    if (tableID) {
        var rowIndex = 0, rowCount = tableID.rows.length;
        while( rowIndex < rowCount ) {
            rowClassName = tableID.rows[rowIndex].className.split(' ')[0];
            if (rowClassName.indexOf('-') === 0){
                rowClassName =  rowClassName.replace('-','');
                CookieArray = CookieValue.split('|');
                for(var i = 0; i < CookieArray.length; i = i + 1){
                    CookieArray[i] = parseInt(CookieArray[i], 16);
                }
                if (filterVisibility(rowClassName,CookieArray) === 0){
                    tableID.rows[rowIndex].style.display = 'none';
                }
            }
            rowIndex = rowIndex + 1;
        }
    }    
}

function filterVisibility(givenMap,selectedMap) {
    var eachGiven = givenMap.split('|');
    var mapFlag = 1;
    for (var i = 0; i < eachGiven.length; i = i + 1) {
        if ((selectedMap[i] > 0) && ((selectedMap[i] & parseInt(eachGiven[i], 16)) == 0)) {
            mapFlag = 0;
            break;
        }
    }
    if (mapFlag == 1){
        FoundMatch = 1;
        return 1;
    }else {
       if(FoundMatch!=1)
        FoundMatch = 0;
        return 0;
    }
}

function getCookie(CookieName) {
    var DocCookie = document.cookie, CookieValue = null, StartIndex = DocCookie.indexOf(' ' + CookieName + '=');
    if (StartIndex == -1){
        StartIndex = DocCookie.indexOf(CookieName + '=');
    }
    if (StartIndex == -1){
        DocCookie = null;
    }else{
      StartIndex=DocCookie.indexOf('=', StartIndex) + 1;
      var EndIndex=DocCookie.indexOf(';', StartIndex);
      var indexOfHiphen = DocCookie.indexOf('-', StartIndex);
      if((indexOfHiphen > -1) && (indexOfHiphen < EndIndex))
         EndIndex = indexOfHiphen;
      if (EndIndex == -1){
         EndIndex=DocCookie.length;}
      CookieValue=unescape(DocCookie.substring(StartIndex,EndIndex));
    }
    return CookieValue;
}

function filterDocContent(defaultMap,cookieName){
    var divArray = document.getElementsByTagName("div");
    var divLen = divArray.length;
    var divIndex = 0;
    var CookieArray;
    var CookieValue = getCookie(cookieName);
    CookieValue = CookieValue ? CookieValue : defaultMap;
    CookieArray = CookieValue.split('|');
    for(var i = 0; i < CookieArray.length; i = i + 1){
        CookieArray[i] = parseInt(CookieArray[i], 16);
    }

    var catMsg = document.getElementById("cat_msg");
    if(catMsg != null)
     catMsg.style.visibility = 'hidden';
    while( divIndex < divLen ) {
        var divClassVal = divArray[divIndex].className;
        var divIdVal = divArray[divIndex].id;
        if((divClassVal != null) && (divClassVal.indexOf('-') === 0) && (divIdVal != null) && (divIdVal.indexOf('-') === 0)){
            divIdVal = divIdVal.replace('-','');
            var msgChapter = document.getElementById("msg_" + divIdVal);
            divClassVal = divClassVal.replace('-','');
            if (filterVisibility(divClassVal,CookieArray) === 0){
                divArray[divIndex].style.display = 'none';
                if(msgChapter != null)
                 msgChapter.style.display = '';
                if(catMsg != null)
                 catMsg.style.visibility = 'visible';
            }
            else
            {
                divArray[divIndex].style.display = '';
                if(msgChapter != null)
                 msgChapter.style.display = 'none';
            }
        }
        divIndex = divIndex + 1;
    }

    var trArray = document.getElementById("toc_1").parentNode.childNodes;
    var index = 0;
    if(trArray.length){
      while(index < trArray.length)
      {
         var trID = trArray[index].id;
         if((trID != null) && ((trID.indexOf("toc_") === 0)))
         {
          trArray[index].style.display = "";
          trID = trID.replace("toc_","");
          var divMatched = document.getElementById("-"+trID);
          if((divMatched != null) && (divMatched.style.display == "none"))
             trArray[index].style.display = "none";
          else if(trID.indexOf(".") > 0){
             var parentCHL = trID.substring(0,trID.lastIndexOf("."));
             var pdivaMatched = document.getElementById("toc_"+parentCHL);
             if((pdivaMatched != null) && (pdivaMatched.style.display == "none"))
              trArray[index].style.display = "none";
          }
         }
         index++;
      }
    }
}