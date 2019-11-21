var async = require('async');

function printAsync(s, cb) {
   var delay = Math.floor((Math.random()*1000)+500);
   setTimeout(function() {
       console.log(s);
       if (cb) cb();
   }, delay);
}

function task1(cb) {
    printAsync("1", function() {
        task2(cb);
    });
}

function task2(cb) {
    printAsync("2", function() {
        task3(cb);
    });
}

function task3(cb) {
    printAsync("3", cb);
}

function loop(n){
	funcs = Array.apply(null, {length: n}).map( any => task1);
	if(n != 0){
	  async.waterfall(funcs, function (error, success) {
	    if (error) { console.log('Error!'); }
	    return console.log('Success');
	});
	}
}

loop(4);
