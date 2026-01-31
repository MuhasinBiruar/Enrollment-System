#!C:/Users/asian/AppData/Local/Programs/Python/Python313/python.exe

import io
import mysql.connector
import sys

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import os
import base64
import urllib.parse
import traceback


def getSqlForStudent(db_connection, strInp : str, intStudid : int):
    try:
        cur = db_connection.cursor()
        cur.execute(f"SELECT {strInp} FROM students where studid = {intStudid}")
        output = cur.fetchone()
        if output:
            return output[0]
    except mysql.connector.Error as err:
        return f"Error: {err}"
    finally:
        if cur:
            cur.close()
def getSqlForSubject(db_connection, strInp : str, intSubjid : int):
    try:
        cur = db_connection.cursor()
        cur.execute(f"SELECT {strInp} FROM subjects where subjid = {intSubjid}")
        output = cur.fetchone()
        if output:
            return output[0]
    except mysql.connector.Error as err:
        return f"Error: {err}"
    finally:
        if cur:
            cur.close()

print("Content-type: text/html; charset=utf-8\n")
print("<html> <head> <title>Student Evaluation Portal</title>")
print("<style>")
print("table {border-collapse: collapse; margin-bottom: 1em;}") # Added margin
#borders
print("th, td{")
print("border: 1px solid black;")
print("padding: 5px 8px;")
print("text-align: left; }")
# Make headers bold and add background
print("th { font-weight: bold; background-color: #f2f2f2; }")

print(
    """
  label {
    display: block; /* Makes the label appear on its own line */
    font-weight: bold;
    margin-bottom: 0.5em;
  }
  textarea {
    width: 90%; /* Makes textarea wide */
    height: 100px; /* Adjusted height */
    padding: 5px;
    margin-bottom: 1em; /* Space before the button */
    border: 1px solid black;
  }
  button {
    padding: 8px 15px;
  }
  .success-message { color: green; font-weight: bold; margin-top: 1em; }
  .error-message { color: red; font-weight: bold; margin-top: 1em; }
  pre { background-color: #eee; padding: 10px; border: 1px solid #ccc; white-space: pre-wrap; word-wrap: break-word; }
"""
)
print("</style>")
print("</head> <body>")

# Initialize variables
conn = None
cursor = None
update_message = "" # For feedback after submit

try:
    # --- Determine request method and get parameters ---
    request_method = os.environ.get('REQUEST_METHOD', 'GET').upper()
    params_source = {}
    evaluation_comment_submitted = None # Store submitted comment here

    if request_method == 'POST':
        # Read data submitted via the comment post
        post_data = sys.stdin.read()
        params_source = urllib.parse.parse_qs(post_data)
        # Get comment from POST data
        evaluation_comment_submitted = params_source.get('evaluationComments', [""])[0]
    else: # GET request (or if POST data reading fails somehow)
        # Read parameters from URL query string
        query_string = os.environ.get('QUERY_STRING', None)
        if not query_string:
            print("<h1>Error</h1><p class='error-message'>Query parameters (like ?db_name=...) are missing.</p></body></html>")
            exit()
        params_source = urllib.parse.parse_qs(query_string)

    # Get encoded IDs and dbNAMEE
    encoded_db_name_str = params_source.get('db_name', [None])[0]
    encoded_s_id_str = params_source.get('s_id', [None])[0]
    encoded_subj_id_str = params_source.get('subj_id', [None])[0]

    # --- CHECK and DECODE parameters ---
    if not encoded_db_name_str or not encoded_s_id_str or not encoded_subj_id_str:
        print("<h1>Error</h1><p class='error-message'>Required parameters ('db_name', 's_id', 'subj_id') missing.</p></body></html>")
        exit()

    try:
        #decode
        db_name = base64.b64decode(encoded_db_name_str).decode('utf-8').strip()
        student_id = base64.b64decode(encoded_s_id_str).decode('utf-8').strip()
        subject_id = base64.b64decode(encoded_subj_id_str).decode('utf-8').strip()
    except Exception as decode_err:
        print(f"<h1>Error</h1><p class='error-message'>Failed to decode Base64 parameters: {decode_err}</p></body></html>")
        exit()

    #Connect to MySQL
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="root",
        database=db_name
    )

    # handle post here
    if request_method == 'POST':
        if evaluation_comment_submitted is not None and student_id and subject_id:
            try:
                cursor = conn.cursor()
                # Prepare the UPDATE statement
                update_sql = "UPDATE enroll SET evaluation = %s WHERE studid = %s AND subjid = %s"
                # Execute with parameters
                cursor.execute(update_sql, (evaluation_comment_submitted, student_id, subject_id))
                conn.commit() # Save changes
                if cursor.rowcount > 0:
                    update_message = "<p class='success-message'>Evaluation updated successfully!</p>"
                else:
                    update_message = "<p class='error-message'>Warning: No matching enrollment record found to update (Student ID: {}, Subject ID: {}).</p>".format(student_id, subject_id)
            except mysql.connector.Error as update_err:
                conn.rollback() # Undo changes on error
                update_message = f"<p class='error-message'>Error updating comment: {update_err}</p>"
            finally:
                if cursor: cursor.close() # Close cursor used for update
        else:
            update_message = "<p class='error-message'>Could not update: Comment or IDs were missing.</p>"
    # end post


    #Body
    print("<h1>Student Evaluation Portal</h1>")
    print("<hr>")
    print("<h2>Student Information</h2>")

    print("<table>")
    print("<tr>")
    print("<th>ID</th>")
    print(f"<th>{student_id}</th>") # Kept Placeholder
            
    print("<th>Course</th>")
    course = getSqlForStudent(conn, "Course", int(student_id))
    print(f"<th>{course}</th>")

    print("</tr>")

    print("<tr>")
    print("<th>Name</th>")
    name = getSqlForStudent(conn, "Name", int(student_id))
    print(f"<th>{name}</th>") # Kept Placeholder

    print("<th>Year Level</th>")
    yearLvl = getSqlForStudent(conn, "Yearlvl", int(student_id))
    print(f"<th>{yearLvl} Year</th>") # Kept Placeholder
    print("</tr>")
    print("</table>")

    print("<h2>Subject Information</h2>")
    print("<table>")
    print("<tr>")
    print("<th>ID</th>")
    print(f"<th>{subject_id}</th>") # Kept Placeholder
    print("</tr>")

    print("<tr>")
    print("<th>Code</th>")
    subjCode = getSqlForSubject(conn, "SubjCode", int(subject_id))
    print(f"<th>{subjCode}</th>") # Kept Placeholder
    print("</tr>")

    print("<tr>")
    print("<th>Description</th>")
    subjDesc = getSqlForSubject(conn, "SubjDesc", int(subject_id))
    print(f"<th>{subjDesc}</th>") # Kept Placeholder
    print("</tr>")

    print("<tr>")
    print("<th>Units</th>")
    subjUnits = getSqlForSubject(conn, "SubjUnits", int(subject_id))
    print(f"<th>{subjUnits}</th>") # Kept Placeholder
    print("</tr>")

    print("<tr>")
    print("<th>Schedule</th>")
    sched = getSqlForSubject(conn, "Schedule", int(subject_id))
    print(f"<th>{sched}</th>") # Kept Placeholder
    print("</tr>")
    print("</table>")


    
    # Display update message if there was one from POST
    if update_message:
        print(update_message)

   
    existing_comment = ""
    try:
        cursor = conn.cursor() # Need a cursor if previous one was closed
        comment_query = "SELECT evaluation FROM enroll WHERE studid = %s AND subjid = %s"
        cursor.execute(comment_query, (student_id, subject_id))
        comment_result = cursor.fetchone()
        if comment_result and comment_result[0] is not None:
             existing_comment = comment_result[0]
    except mysql.connector.Error as comment_err:
         if update_message: update_message += "<br>" # Add line break if msg exists
         update_message += f"<p class='error-message'>Error fetching existing comment: {comment_err}</p>"
         print(update_message) # Print error fetching comment immediately
    finally:
        if cursor: cursor.close()


    # Action points to the script itself, method is POST
    print(f'<form action="evaluate.py" method="post">')

    # IMPORTANT: Include hidden fields to pass parameters back on submit
    print(f'<input type="hidden" name="s_id" value="{encoded_s_id_str}">')
    print(f'<input type="hidden" name="subj_id" value="{encoded_subj_id_str}">')
    print(f'<input type="hidden" name="db_name" value="{encoded_db_name_str}">')

    print('<label for="evaluationComments">Your Evaluation/Comments:</label>')

    import html
    print(f'<textarea id="evaluationComments" name="evaluationComments" placeholder="Enter your thoughts here...">{html.escape(existing_comment)}</textarea>')
    print('<button type="submit">Submit Comment</button>')
    print("</form>")


except mysql.connector.Error as err:
    # Print database errors
    print("<h1>Python CGI Test FAILED</h1>")
    print(f"<p style='color: red;'>Database connection or query error:</p><pre>{err}</pre>")
    print("<p>Check credentials, DB/table names, and MySQL service.</p>")

except Exception as e:
    # Print other Python errors
    print("<h1>Python CGI Test FAILED</h1>")
    print(f"<p style='color: red;'>An unexpected Python error occurred:</p><pre>")
    traceback.print_exc()
    print("</pre>")

finally:
    if 'conn' in locals() and conn and conn.is_connected():
        conn.close()


print("</body></html>")