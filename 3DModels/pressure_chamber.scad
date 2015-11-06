//high_quality=true;
bottom_cyl_dia=15; // mm
bottom_cyl_height=5;
top_cyl_dia=10;
top_cyl_height=5;
screen_cutout=4;
screw_dia=1;
pressure_tube_dia=2;
$fn=150;

module makeBottom(){
    cylinder(r=bottom_cyl_dia, h=bottom_cyl_height);
}

module makeTop(){
    translate([0,0,bottom_cyl_height])cylinder(r1=top_cyl_dia, r2=top_cyl_dia*0.8, h=bottom_cyl_height);
}

module makeBase(){
    makeBottom();
    makeTop();
}
module cutHoles(){
    
    difference(){
        makeBase();
        cylinder(r=screen_cutout,h=bottom_cyl_height+top_cyl_height);
        translate([0,13,0]) cylinder(r=screw_dia,h=bottom_cyl_height);
        translate([0,-13,0]) cylinder(r=screw_dia,h=bottom_cyl_height);
        translate([13,0,0]) cylinder(r=screw_dia,h=bottom_cyl_height);
        translate([-13,0,0]) cylinder(r=screw_dia,h=bottom_cyl_height);
        translate([0,0,bottom_cyl_height+top_cyl_height/2])rotate([90,0,0])cylinder(r=pressure_tube_dia/2,h=10);
    }
}
module makeHalfChamber(){
    cutHoles();
}

module makeShowModel(spaced, spacing=2.5){
    if (spaced){
        translate([spacing,0,0])rotate([0,90,0])makeHalfChamber();
        translate([-spacing,0,0])rotate([0,-90,0])makeHalfChamber();
    }
    else{
        rotate([0,90,0])makeHalfChamber();
        rotate([0,-90,0])makeHalfChamber();
    }
    
}

makeShowModel(spaced=true);
//makeHalfChamber();