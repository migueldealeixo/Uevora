var renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

var camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 1000);
camera.position.set(0, 100, 300);
camera.lookAt(0, 0, 0);

var scene = new THREE.Scene();

var light = new THREE.PointLight(0xffffff, 1, 500);
light.position.set(50, 50, 100);
scene.add(light);

var ambientLight = new THREE.AmbientLight(0x404040);
scene.add(ambientLight);

var floor = new THREE.PlaneGeometry(1000, 1000);
var floorMaterial = new THREE.MeshLambertMaterial({ color: 0x503009 });
var floorMesh = new THREE.Mesh(floor, floorMaterial);
floorMesh.rotation.x = -Math.PI / 2;
floorMesh.position.y = -50;
scene.add(floorMesh);

var extrudeSettings = {
    steps: 2,
    depth: 2,
    bevelEnabled: true,
    bevelThickness: 1,
    bevelSize: 1,
    bevelSegments: 1
};

function createNingados() {
    const letters = [
        { shape: letterN, offset: 0 },
        { shape: letterI, offset: 0 },
        { shape: letterN, offset: 0 },
        { shape: letterG, offset: 0 },
        { shape: letterA, offset: 0 },
        { shape: letterD, offset: 0 },
        { shape: letterO, offset: 0 },
        { shape: letterS, offset: 0 }
    ];

    let currentX = -120; // Posição inicial (ajuste conforme necessário).
    const spacing = 25; // Espaçamento base entre as letras.

    letters.forEach((letterObj, index) => {
        const mesh = letterObj.shape();
        mesh.position.set(currentX, -3, 0);

        const targetPosition = new THREE.Vector3(currentX, -3, 0);
        const tween = new TWEEN.Tween(mesh.position).to(targetPosition, 1000);
        tween.start();

        scene.add(mesh);

        currentX += spacing; // Avançar posição para próxima letra.
    });
}

function letterN() {
    var path_N = new THREE.Shape();
    path_N.moveTo(0, 0);
    path_N.lineTo(0, 20);
    path_N.lineTo(10, 20);
    path_N.lineTo(10, 5);
    path_N.lineTo(15, 20);
    path_N.lineTo(20, 20);
    path_N.lineTo(20, 0);
    path_N.lineTo(15, 0);
    path_N.lineTo(10, 15);
    path_N.lineTo(10, 0);
    path_N.lineTo(0, 0);

    var geometry = new THREE.ExtrudeGeometry(path_N, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function letterI() {
    var path_I = new THREE.Shape();
    path_I.moveTo(0, 0);
    path_I.lineTo(0, 20);
    path_I.lineTo(10, 20);
    path_I.lineTo(10, 0);
    path_I.lineTo(0, 0);

    var geometry = new THREE.ExtrudeGeometry(path_I, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function letterG() {
    var path_G = new THREE.Shape();
    path_G.moveTo(20, 0);
    path_G.lineTo(0, 0);
    path_G.lineTo(0, 20);
    path_G.lineTo(20, 20);
    path_G.lineTo(20, 10);
    path_G.lineTo(10, 10);
    path_G.lineTo(10, 12);
    path_G.lineTo(15, 12);
    path_G.lineTo(15, 8);
    path_G.lineTo(5, 8);
    path_G.lineTo(5, 12);
    path_G.lineTo(20, 12);
    path_G.lineTo(20, 0);

    var geometry = new THREE.ExtrudeGeometry(path_G, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function letterA() {
    var path_A = new THREE.Shape();
    path_A.moveTo(10, 0);
    path_A.lineTo(0, 20);
    path_A.lineTo(5, 20);
    path_A.lineTo(7, 14);
    path_A.lineTo(13, 14);
    path_A.lineTo(15, 20);
    path_A.lineTo(20, 20);
    path_A.lineTo(10, 0);
    path_A.lineTo(7, 10);
    path_A.lineTo(13, 10);
    path_A.lineTo(10, 0);

    var geometry = new THREE.ExtrudeGeometry(path_A, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function letterD() {
    var path_D = new THREE.Shape();
    path_D.moveTo(0, 0);
    path_D.lineTo(0, 20);
    path_D.lineTo(10, 20);
    path_D.bezierCurveTo(15, 15, 15, 5, 10, 0);
    path_D.lineTo(0, 0);
    path_D.lineTo(5, 2);
    path_D.lineTo(5, 18);
    path_D.lineTo(10, 18);
    path_D.bezierCurveTo(12, 15, 12, 5, 10, 2);
    path_D.lineTo(5, 2);

    var geometry = new THREE.ExtrudeGeometry(path_D, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function letterO() {
    var path_O = new THREE.Shape();
    path_O.moveTo(10, 0);
    path_O.bezierCurveTo(0, 5, 0, 15, 10, 20);
    path_O.bezierCurveTo(20, 15, 20, 5, 10, 0);
    path_O.lineTo(7, 2);
    path_O.bezierCurveTo(3, 6, 3, 14, 7, 18);
    path_O.lineTo(10, 20);
    path_O.lineTo(13, 18);
    path_O.bezierCurveTo(17, 14, 17, 6, 13, 2);
    path_O.lineTo(10, 0);

    var geometry = new THREE.ExtrudeGeometry(path_O, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function letterS() {
    var path_S = new THREE.Shape();
    path_S.moveTo(0, 0);
    path_S.lineTo(20, 0);
    path_S.lineTo(20, 8);
    path_S.bezierCurveTo(15, 10, 10, 12, 15, 15);
    path_S.bezierCurveTo(20, 18, 5, 20, 5, 12);
    path_S.lineTo(0, 0);

    var geometry = new THREE.ExtrudeGeometry(path_S, extrudeSettings);
    var material = new THREE.MeshLambertMaterial({ color: 0xF3FFE2 });
    return new THREE.Mesh(geometry, material);
}

function animate(time) {
    requestAnimationFrame(animate);
    TWEEN.update(time);

    scene.rotation.y += 0.002;
    renderer.render(scene, camera);
}

createNingados();
animate();
