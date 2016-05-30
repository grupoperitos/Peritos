<?php ob_start(); ?>
<?php
$htm = $_GET["html"];
?>


<html lang="en">
<head>
    <meta charset="utf-8" />
    <title>EPOCAPP | Tratamiento</title>
</head>
<body>

<?php echo $htm; ?>

</body>
</html>


<?
require_once("dompdf/dompdf_config.inc.php");
$dompdf = new DOMPDF();
$dompdf->load_html(ob_get_clean());
$dompdf->render();
$pdf = $dompdf->output();
$filename = "Tratamiento".time().'.pdf';
file_put_contents($filename, $pdf);
$dompdf->stream($filename);
?>