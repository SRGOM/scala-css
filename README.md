scala-css
==

Since scala-js has made sure that you use the same class names across JS and HTML in a safe manner, css has remained the only weak link. This is a POC to do the same for CSS, i.e. Define class names in a shared directory, and use them in HTML and CSS both.

- The code is all over the place! 
- What does it achieve? Shared directory can hold classes and it can be reused in scala-js and scala-jvm. IDE tells you what's used where.
- What this does not solve- using a class for HTML not realizing you've not styled it?
- The safety of individual valued properties is just a side-effect. Although surprisingly my CSS and even bootstrap has a lot of use of these, so typesafety can be handy...
- Plan is for this to run in its own proj, and auto compile on change. No on demand-style
