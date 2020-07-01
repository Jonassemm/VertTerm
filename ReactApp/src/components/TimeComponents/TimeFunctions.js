var moment = require('moment'); 

export function setDate() {
    const date = new Date();
    var changeHours = false;

    date.setSeconds(0);

    var minutes = date.getMinutes();
    if (minutes < 15) {
        date.setMinutes(15);
    } else if (minutes < 30) {
        date.setMinutes(30);
    } else if (minutes < 45) {
        date.setMinutes(45);
    } else if (minutes <= 59) {
        date.setMinutes(0);
        //changeHours = true;
    }

   /*  TESTS
    var ds = "13.01.2003 13:45";
    var date = moment(ds, "DD.MM.yyyy HH:mm").toDate();
    var dateString = moment(date).format("DD.MM.YYYY HH:mm").toString();
    */

    const resultDate = moment(date).format("DD.MM.YYYY HH:mm").toString()
    return resultDate;
}