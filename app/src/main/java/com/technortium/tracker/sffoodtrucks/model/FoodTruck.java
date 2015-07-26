package com.technortium.tracker.sffoodtrucks.model;

public class FoodTruck {
    /*
    *  {
    "address": "58 MAIN ST",
    "applicant": "The Sandwich Stand, LLC.",
    "approved": "2015-04-27T15:56:57.000",
    "block": "3711",
    "blocklot": "3711005",
    "cnn": "8627000",
    "expirationdate": "2016-03-15T00:00:00.000",
    "facilitytype": "Push Cart",
    "fooditems": "Vietnamese sandwiches: spring rolls: bottle water: can soda: chicken and shrimp rice noodles: rice with chicken: chicken salad: noodle soup.",
    "latitude": "37.792109338609",
    "location": {
      "type": "Point",
      "coordinates": [
        -122.395804,
        37.792109
      ]
    },
    "locationdescription": "MAIN ST: MARKET ST to MISSION ST (1 - 99)",
    "longitude": "-122.395803865502",
    "lot": "005",
    "objectid": "635282",
    "permit": "15MFF-0108",
    "priorpermit": "0",
    "received": "Mar 25 2015 12:24PM",
    "schedule": "http://bsm.sfdpw.org/PermitsTracker/reports/report.aspx?title=schedule&report=rptSchedule&params=permit=15MFF-0108&ExportPDF=1&Filename=15MFF-0108_schedule.pdf",
    "status": "APPROVED",
    "x": "6013902.41",
    "y": "2116435.193"
    }
    *
    * */
    private String address;
    private String applicant;
    private String approved;
    private String block;
    private String blocklot;
    private String cnn;
    private String expirationdate;
    private String facilitytype;
    private String fooditems;
    private double latitude;
    private Location location;
    private String locationdescription;
    private double longitude;
    private String lot;
    private String objectid;
    private String permit;
    private String priorpermit;
    private String received;
    private String schedule;
    private String status;
    private String x;
    private String y;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBlocklot() {
        return blocklot;
    }

    public void setBlocklot(String blocklot) {
        this.blocklot = blocklot;
    }

    public String getCnn() {
        return cnn;
    }

    public void setCnn(String cnn) {
        this.cnn = cnn;
    }

    public String getExpirationdate() {
        return expirationdate;
    }

    public void setExpirationdate(String expirationdate) {
        this.expirationdate = expirationdate;
    }

    public String getFacilitytype() {
        return facilitytype;
    }

    public void setFacilitytype(String facilitytype) {
        this.facilitytype = facilitytype;
    }

    public String getFooditems() {
        return fooditems;
    }

    public void setFooditems(String fooditems) {
        this.fooditems = fooditems;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationdescription() {
        return locationdescription;
    }

    public void setLocationdescription(String locationdescription) {
        this.locationdescription = locationdescription;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public String getPriorpermit() {
        return priorpermit;
    }

    public void setPriorpermit(String priorpermit) {
        this.priorpermit = priorpermit;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "FoodTruck{" +
                "address='" + address + '\'' +
                ", applicant='" + applicant + '\'' +
                ", approved='" + approved + '\'' +
                ", block='" + block + '\'' +
                ", blocklot='" + blocklot + '\'' +
                ", cnn='" + cnn + '\'' +
                ", expirationdate='" + expirationdate + '\'' +
                ", facilitytype='" + facilitytype + '\'' +
                ", fooditems='" + fooditems + '\'' +
                ", latitude='" + latitude + '\'' +
                ", location=" + location +
                ", locationdescription='" + locationdescription + '\'' +
                ", longitude='" + longitude + '\'' +
                ", lot='" + lot + '\'' +
                ", objectid='" + objectid + '\'' +
                ", permit='" + permit + '\'' +
                ", priorpermit='" + priorpermit + '\'' +
                ", received='" + received + '\'' +
                ", schedule='" + schedule + '\'' +
                ", status='" + status + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}
