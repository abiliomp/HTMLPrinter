<h1>HTMLPrinter</h1>
<p>A simple and easy to use program and websocket service to print HTML content directly into a configured printer. The aim is to provide a flexible, reliable and precise printing solution to web-based systems.</p>
<p>I hope you find it useful. Any feedback will be appreciated.</p>
<em>Abilio</em>

<h3>CLI Usage</h3>
<p>You can use one use one release from the "Releases" section or you can compile the program for the Java source code.</p>
<p>On calling the program you &lt;must&gt;/[can] add the following arguments:</p>
<p>&lt;HTMLFilePath&gt; [option1] [option1 argument] ...</p>
<p>Options:</p>
<ul>
  <li>-h or --help : displays the help.</li>
  <li>--init : initializes the configuration XML file.</li>
  <li>--config &lt;XMLConfigFilePath&gt; : sets a custom configuration absolute file path (including file name). Default path is the JAR's folder.</li>
  <li>-p or --printer <PrinterName> : specifies the printer id to be used. The printer must be within the configuration file.</li>
  <li>--css &lt;CSSFilePath&gt; : specifies an aditional CSS file to be applied.</li>
  <li>--charset &lt;CharsetName&gt; : sets the charset to read the files (default UTF8).</li>
  <li>--to-pdf &lt;PDFFilePath&gt; : generates a PDF document to be stored in the specified path.</li>
  <li>--error-log : enables the error logging functionality. The program will generate one .log file with the same path of the HTML file.</li>
  <li>-s or --run-service : run the program as a service daemon. This parameter will discard the HTMLFilePath if specified.</li>
  <li>-w <port> or --websocket-port <port> : sets the service daemon websocket TCP port number. Default is 3333."</li>
</ul>
<p>Supported Charsets: UTF8, UTF16, ASCII and 8859."</p>

<h3>Websockets Usage</h3>
<p>On the server side (were the printers are configured and connected) run the program with the -s option. Once running just connect your webapp to websocket.</p>
<p>The service expects a print request expressed as an JSON object sent through the websocket. The general structure of the object is:</p>
<code style="display:block; white-space:pre-wrap">
{
    "printerId": "APrinter",
    "charset": "UTF-8",
    "html": "&lthtml&gt&lthead&gt&lt/head&gt&ltbody&gt&lth1&gtExample HTML&lt/h1&gt&ltp style=\"position: absolute; top: 31mm; left: 33mm;\"&gtThis is an example.&lt/p&gt&lt/body&gt&lt/html&gt"
}
</code>
    
<h3>License</h3>
<em>The MIT License</em>
<p>Copyright 2021 Networkers SRL and @abiliomp.</p>
<p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:</p>
<p>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.</p>
<p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.</p>
