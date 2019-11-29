// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy.
// 4. Zaimplementuj rozwiązanie z kelnerem (według książki Programowanie współbiezne i rozproszone)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp
//    do widelców. Wyniki przedstaw na wykresach.

const async = require('async');

const eatTime = 100;
const thinkTime = 100;
const waiterQueue = [];

var Fork = function () {
    this.state = 0;
    return this;
};

Fork.prototype.acquire = function (cb) {
    // zaimplementuj funkcję acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwszą próbą podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy próba jest nieudana, zwiększa czas oczekiwania dwukrotnie
    //    i ponawia próbę, itd.
    var time = 1;
    var getForkWithTimeout = function (waitTime, fork) {
        setTimeout(function () {
            if (fork.state === 1) {
                time *= 2;
                if (time > 1024) {
                    time = 1;
                }
                getForkWithTimeout(Math.floor(Math.random() * time), fork);
            } else {
                fork.state = 1;
                if (cb) cb();
            }
        }, waitTime);
    };
    getForkWithTimeout(1, this);
};

Fork.prototype.release = function () {
    this.state = 0;
};

var Philosopher = function (id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id + 1) % forks.length;
    return this;
};

Philosopher.prototype.startNaive = function (count) {
    // zaimplementuj rozwiązanie naiwne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;
    if (count > 0) {
        setTimeout(function () {
            console.log("Philosopher " + id + " thinking (count left = " + count + ")");
            forks[f1].acquire(function () {
                console.log("Philosopher " + id + " fork nr" + f1 + " accquired");
                forks[f2].acquire(function () {
                    console.log("Philosopher " + id + " fork nr" + f2 + " accquired");
                    setTimeout(function () {
                        console.log("Philosopher " + id + " eating");
                        forks[f1].release();
                        forks[f2].release();
                        philosophers[id].startNaive(count - 1);
                    }, Math.floor(Math.random() * eatTime));

                })
            });
        }, Math.floor(Math.random() * thinkTime));
    } else {
        console.log("Philosopher " + id + " I'm done with eating");
    }
};

Philosopher.prototype.startAsym = function (count) {
    // zaimplementuj rozwiązanie asymetryczne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;
    if (id % 2 === 1) {
        const tmp = f2;
        f2 = f1;
        f1 = tmp;
    }
    if (count > 0) {
        setTimeout(function () {
            console.log("Philosopher " + id + " thinking (count left = " + count + ")");
            forks[f1].acquire(function () {
                console.log("Philosopher " + id + " fork nr" + f1 + " accquired");
                forks[f2].acquire(function () {
                    console.log("Philosopher " + id + " fork nr" + f2 + " accquired");
                    setTimeout(function () {
                        console.log("Philosopher " + id + " eating");
                        forks[f1].release();
                        forks[f2].release();
                        philosophers[id].startAsym(count - 1);
                    }, Math.floor(Math.random() * eatTime));

                })
            });
        }, Math.floor(Math.random() * thinkTime));
    } else {
        console.log("Philosopher " + id + " I'm done with eating");
    }
};

acquireWaiter = function (fn1, fn2, forks, cb) {
    var f1 = forks[fn1],
        f2 = forks[fn2];

    setTimeout(function () {
        if (f1.state === 1 || f2.state === 1) {
            waiterQueue.push({fn1: fn1, fn2:fn2, forks:forks, cb: cb});
        } else {
            f1.state = 1;
            f2.state = 1;
            if (cb) cb();
        }
    }, 1);
};

releaseWaiter = function (fn1, fn2, forks) {
    var f1 = forks[fn1],
        f2 = forks[fn2];
    f1.state = 0;
    f2.state = 0;
    var emptyWaiterQueue = function () {
        if (waiterQueue.length !== 0) {
            var newfn1   = waiterQueue[0].fn1,
                newfn2   = waiterQueue[0].fn2,
                newforks = waiterQueue[0].forks,
                cb = waiterQueue[0].cb;
            f1 = newforks[newfn1];
            f2 = newforks[newfn2];
            if (f1.state === 0 && f2.state === 0) {
                waiterQueue.shift();
                f1.state = 1;
                f2.state = 1;
                if (cb) cb();
                emptyWaiterQueue();
            }
        }
    };
    emptyWaiterQueue();
};

Philosopher.prototype.startConductor = function (count) {
    // zaimplementuj rozwiązanie z kelnerem
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;
    if (count > 0) {
        setTimeout(function () {
            console.log("Philosopher " + id + " thinking (count left = " + count + ")");
            acquireWaiter(f1, f2, forks, function () {
                console.log("Philosopher " + id + " fork nr" + f1 + "," + f2 + " accquired");
                setTimeout(function () {
                    console.log("Philosopher " + id + " eating");
                    releaseWaiter(f1, f2, forks);
                    philosophers[id].startConductor(count - 1);
                }, Math.floor(Math.random() * eatTime));
            });
        }, Math.floor(Math.random() * thinkTime));
    } else {
        console.log("Philosopher " + id + " I'm done with eating");
    }
};


// Algorytm BEB powinien obejmować podnoszenie obu widelców, a nie każdego z osobna

acquireBoth = function (f1, f2, cb) {
    var time = 1;
    var getForkWithTimeout = function (waitTime, fork) {
        setTimeout(function () {
            if (f1.state === 1 || f2.state === 1) {
                time *= 2;
                if (time > 1024) {
                    time = 1;
                }
                getForkWithTimeout(Math.floor(Math.random() * time), fork);
            } else {
                f1.state = 1;
                f2.state = 1;
                if (cb) cb();
            }
        }, waitTime);
    };
    getForkWithTimeout(1, this);
};


Philosopher.prototype.startBoth = function (count) {
    // zaimplementuj rozwiązanie asymetryczne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;
    if (count > 0) {
        setTimeout(function () {
            console.log("Philosopher " + id + " thinking (count left = " + count + ")");
            acquireBoth(forks[f1], forks[f2], function () {
                console.log("Philosopher " + id + " fork nr" + f1 + "," + f2 + " accquired");
                setTimeout(function () {
                    console.log("Philosopher " + id + " eating");
                    forks[f1].release();
                    forks[f2].release();
                    philosophers[id].startBoth(count - 1);
                }, Math.floor(Math.random() * eatTime));
            });
        }, Math.floor(Math.random() * thinkTime));
    } else {
        console.log("Philosopher " + id + " I'm done with eating");
    }
};


var N = 5;
var forks = [];
var philosophers = [];
for (let i = 0; i < N; i++) {
    forks.push(new Fork());
}

for (let i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks));
}

// Naiwne (mamy blokade)
// for (let i = 0; i < N; i++) {
//     philosophers[i].startNaive(10);
// }

// Asymetryczne
// for (let i = 0; i < N; i++) {
//     philosophers[i].startAsym(10);
// }

// Z podnoszeniem 2 jednocześnie (mamy głodzenie, nie ma blokady)
// for (let i = 0; i < N; i++) {
//     philosophers[i].startBoth(10);
// }

// Z kelnerem
// for (let i = 0; i < N; i++) {
//     philosophers[i].startConductor(10);
// }
