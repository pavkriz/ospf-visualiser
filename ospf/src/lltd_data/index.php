<?php
function polozkySeznamu() {
    $db = new PDO('sqlite:vyletnik.sqlite'); 
    $q = "select * from vylet order by nazev;";
    $i = 1;
    
	//vypis
    foreach ($db->query($q) as $row) {
        echo "\n";
        //odliseni kazdeho druheho radku barvou
        if ($i % 2 == 0)
            echo "<tr class=\"odd\">";
        else 
			echo "<tr class=\"even\">";

        //cislo
        echo "<td align=\"right\">" . $i++ . "</td><td align=\"center\"";

        //pridani smile pro navstivene lokality
        if ($row['datum'] != null) {
            echo " class=\"smile\">&#9786;";
        } else {
			echo ">";
		}
        echo "</td>" . "<td><a href=\"detail.php?id=" . $row['id'] . "\">" . $row['nazev'] . "</a></td>"
        . "<td align=\"center\"><a href=\"" . $row['odkaz'] . "\" target=\"_blank\" >zde</a></td>"
        . "<td>" . $row['poznamka'] . "</td><td>" . $row['datum'] . "</td>";
        if ($row['souradnice'] == null) {
			echo "<td class=\"sad\">&#9785;</td>";
		} else {
			echo "<td>OK</td>";
		}
		
		echo "<td class=\"delete\"><a href=\"#\" onclick=\"smazatVylet(" . $row['id'] . ");\">&#10006;</a></td>";
        echo "</tr>";
    }
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>BROWSE LLTD DATA</title>
</head>
<body>
<table>
		<colgroup>
			<col width="2%">
			<col width="2%">
			<col width="25%">
			<col width="2%">
			<col width="55%">
			<col width="2%">
			<col width="2%">
			<col width="2%">
		</colgroup>
		<tr>
			<th>&nbsp;</th>
            <th>&nbsp;</th>
            <th>Název</th>
            <th>Odkaz</th>
            <th>Poznámka</th>
            <th>Návštěva</th>
			<th>GPS</th>
			<th>&nbsp;</th>
		</tr>
		<?php polozkySeznamu() ?>
	</table>
<?php
echo "My first PHP script!";
?>

</body>
</html>
