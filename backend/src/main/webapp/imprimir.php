<?php ob_start(); ?>
<?php
$htm = $_POST["html"];

require_once("dompdf/dompdf_config.inc.php");
$dompdf = new DOMPDF();
$dompdf->load_html($htm);
$dompdf->render();
$pdf = $dompdf->output();
$filename = "Tratamiento".time().'.pdf';
file_put_contents($filename, $pdf);
$dompdf->stream($filename);
?>