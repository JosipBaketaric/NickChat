<?php
require 'Config.php';


    function getNick($con){
        $temp = 1;
        $i = 0;
        $response[$i]="NO_ONLINE_USERS";

        $statement = mysqli_prepare($con, "SELECT nick FROM users WHERE loged = ?");
        mysqli_stmt_bind_param($statement, "i",$temp);
        mysqli_stmt_execute($statement);
        mysqli_stmt_bind_result($statement ,$nick);

        while (mysqli_stmt_fetch($statement)) {
            $response[$i] = $nick;
            $i++;
            $fHasUsers = 1;
        }
        if($i > 0){
            $response["error"] = false;
            $response["nUsers"] = $i;
            echo json_encode($response);
        }
        else{
            $response["error"] = true;
            $response["error_msg"] = "No online users";
            echo json_encode($response);
        }

    }




    $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE,DB_PORT);
    if(!$con){
        $response["error"] = true;
        $response["error_msg"] = "Database connection failed";
        echo json_encode($response);
        die;
    }

    $response = array();
    $response["error"] = false;


    getNick($con);
    mysqli_close($con);
    die;

?>