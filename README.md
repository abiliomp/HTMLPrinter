<h1>HTMLPrinter</h1>
<p>A simple and easy to use program to print HTML content directly into a preconfigured printer. The aim is to provide a flexible, reliable and precise printing solution to web-based systems.</p>
<p>I hope you find it useful. Any feedback will be great to receive.</p>
<em>Abilio</em>

<h3>Usage</h3>
<p>You can use one use one release from the "releases" directory or you can compile the program for the Java source code.</p>
<p>On calling the program you &lt;must&gt;/[can] add the following arguments:</p>
<p>&lt;HTMLFilePath&gt; [option1] [option1 argument] ...</p>
<p>Options:</p>
<ul>
  <li>-h or --help : displays the help.</li>
  <li>--css &lt;CSSFilePath&gt; : specifies an aditional CSS file to be applied.</li>
  <li>--charset &lt;CharsetName&gt; : sets the charset to read the files (default UTF8).</li>
  <li>--config &lt;XMLConfigFilePath&gt; : sets a custom configuration absolute file path (including file name). Default path is the JAR's folder.</li>
  <li>--init : initializes the configuration XML file.</li>
  <li>--to-pdf &lt;PDFFilePath&gt; : generates a PDF document to be stored in the specified path.</li>
  <li>--error-log : enables the error logging functionality. The program will generate one .log file with the same path of the HTML file.</li>
</ul>
<p>Supported Charsets: UTF8, UTF16, ASCII and 8859."</p>
