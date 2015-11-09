//high_quality=true;
bottom_cyl_dia=57.03; // mm
bottom_cyl_height=8.34;
top_cyl_dia=34;
top_cyl_height=30;
screen_cutout=20;
screw_dia=4;
pressure_tube_dia=4;
$fn=50;
//31.2
//29.77
module makeBottom(){
    cylinder(r=bottom_cyl_dia/2, h=bottom_cyl_height);
}

module makeTop(){
    translate([0,0,bottom_cyl_height])cylinder(r1=top_cyl_dia/2, r2=29.77/2, h=bottom_cyl_height);
}

module makeBase(){
    makeBottom();
    makeTop();
}
module cutHoles(){
    screw_translate=(bottom_cyl_dia/2)-5;
    difference(){
        makeBase();
        cylinder(r=screen_cutout/2,h=bottom_cyl_height+top_cyl_height);
        translate([0,screw_translate,0]) cylinder(r=screw_dia/2,h=bottom_cyl_height);
        translate([0,-screw_translate,0]) cylinder(r=screw_dia/2,h=bottom_cyl_height);
        translate([screw_translate,0,0]) cylinder(r=screw_dia/2,h=bottom_cyl_height);
        translate([-screw_translate,0,0]) cylinder(r=screw_dia/2,h=bottom_cyl_height);
        translate([0,0,(bottom_cyl_height+top_cyl_height/2)-10])rotate([90,0,0])cylinder(r=pressure_tube_dia/2,h=30);
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