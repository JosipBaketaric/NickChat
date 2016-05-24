<?php
require 'Config.php';




    function deleteUser($con, $nick){
        $statement = mysqli_prepare($con, "DELETE FROM users WHERE nick = ?");
        mysqli_stmt_bind_param($statement, "s",$nick);
        if(mysqli_stmt_execute($statement)){
            return true;
        } else{
            return false;
        }
    }


    $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE,DB_PORT);
    if(!$con){
        $response["error"] = true;
        $response["error_msg"] = "Database connection failed";
        echo json_encode($response);
        die;
    }

    $nick = $_POST["nick"];

    $response = array();
    $response["error"] = false;



    if(!deleteUser($con, $nick))
    {
        $response["error"] = true;
        $response["error_msg"] = "Couldn't delete user.";
        echo json_encode($response);
        die;
    }
    $response["error"] = false;
    $response["msg"] = "User Removed!";
    echo json_encode($response);

    mysqli_close($con);

?>