INSERT INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
VALUES ('1990-05-15', 'john.doe@email.com', 'John Doe', '$2a$10$AfYOsevblqZI7f3XHc7YsuecapX4tqa2PAY.89sRjLCTybkZODegu', '555-123-4567'), -- pass123
       ('1985-09-20', 'jane.smith@email.com', 'Jane Smith', '$2a$10$XVNFtsIweh7lgaeGP/Rvl.CPCYLsowPmWxp5hWEkqMbFzAZ6dW09C', '555-987-6543'), -- abc456
       ('1978-03-08', 'bob.jones@email.com', 'Bob Jones', '$2a$10$ycOlJJ2uXaKWfajHv/N5a.QHtPZRGnCpcsXI60mxwoUsne2NlF1Gi', '555-321-6789'), -- qwerty789
       ('1982-11-25', 'alice.white@email.com', 'Alice White', '$2a$10$fN6EdRfsFDgFlfNTF/awje0wa/aEtcSnoMK7be7u/jrRYUkzhOFbC', '555-876-5432'), -- secret567
       ('1995-07-12', 'mike.wilson@email.com', 'Mike Wilson', '$2a$10$/Xu9zTKvnm0EtxLBzFru1O6aGdRU/ej/M1kXSoJ2vffFaGLeZ3duO', '555-234-5678'), -- mypassword
       ('1989-01-30', 'sara.brown@email.com', 'Sara Brown', '$2a$10$EYj.Ga1n.jXY9Hrk3MwH7.aj8WBCDyq9aotBqQVUkE3J31Q7d6HBC', '555-876-5433'), -- letmein123
       ('1975-06-18', 'tom.jenkins@email.com', 'Tom Jenkins', '$2a$10$LfVIJdPfyBRBV4rqbDn0z.SuDe1rHbeIbCzP1OPaYSgqZSVleC8rK', '555-345-6789'), -- pass4321
       ('1987-12-04', 'lisa.taylor@email.com', 'Lisa Taylor', '$2a$10$zuWCGY.6ckSkoHo708bv5etk4QvJlE0X1Oer/Az8Pzn96tnXrU1Wu', '555-789-0123'), -- securepwd
       ('1992-08-22', 'david.wright@email.com', 'David Wright', '$2a$10$YCr4DOSGjz73r3FGjWmky.igq6zXmbhtY3SDt16Yr78hX0UHcscX6', '555-456-7890'), -- access123
       ('1980-04-10', 'emily.harris@email.com', 'Emily Harris', '$2a$10$mLccOT02MDhAzXwIFudESOYlOBB/4IzEviBiJFUZiE0EipPLbPsVa', '555-098-7654'); -- 1234abcd

INSERT INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
VALUES (1000.00, 'client1@example.com', 'Medelyn Wright', '$2a$10$43XzpvsWUrVZVJtsJM8e1eUdhes6Hlaso.6dv3prgNc54lE2xJo9q'), -- password123
       (1500.50, 'client2@example.com', 'Landon Phillips', '$2a$10$yCQ8JRE5LavOALJRDGUC/uVa07NhYjhpLvQFCdkCLjlK/8RCsiDXi'), -- securepass
       (800.75, 'client3@example.com', 'Harmony Mason', '$2a$10$IOKcYdf6kHJIUu1teEneNOUNOBPxYnkqDaorOsAlPkeQIkzinx/ji'), -- abc123
       (1200.25, 'client4@example.com', 'Archer Harper', '$2a$10$J8YMS3GbUTAAIo379RMxPureKIMgPIFWck0gHA4qjxhVku/kCPH0'), -- pass456
       (900.80, 'client5@example.com', 'Kira Jacobs', '$2a$10$CEtuoXZoRZfJhIKhpm6lUOe5Gvdo68dy0GxVj4zl9KhtoM59sADqe'), -- letmein789
       (1100.60, 'client6@example.com', 'Maximus Kelly', '$2a$10$q0Qk3FLAWRQzdB/bxal5eOrpZOCVY7OpV2OXd7bm2mgNaFpTDn7H6'), -- adminpass
       (1300.45, 'client7@example.com', 'Sierra Mitchell', '$2a$10$CTkOj2ca6Hq.hRrb7EFuX.oUZ1rKwdqzrmUTwZ46BfO4re5OIEPUq'), -- mypassword
       (950.30, 'client8@example.com', 'Quinton Saunders', '$2a$10$ISNmPmTBIAszbtr2BWAg..retkHRfYHVPs5hUPm4Nd6ilIg/U//c6'), -- test123
       (1050.90, 'client9@example.com', 'Amina Clarke', '$2a$10$/1YnXvfR47YAxmGzQMW/geazyczqNjCXYyk5JCEWRr/rfbgb2xBBy'), -- qwerty123
       (880.20, 'client10@example.com', 'Bryson Chavez', '$2a$10$w7tpZsA3D39r4pOjdhD51eDm7tPagxhnyLEeC3z6n51f0VGKD.jJG'); -- pass789

INSERT INTO BOOKS (name, genre, age_group, price, publication_year, author, number_of_pages, characteristics, description, language)
VALUES ('The Hidden Treasure', 'Adventure', 'ADULT', 24.99, '2018-05-15', 'Emily White', 400, 'Mysterious journey', 'An enthralling adventure of discovery', 'ENGLISH'),
       ('Echoes of Eternity', 'Fantasy', 'TEEN', 16.50, '2011-01-15', 'Daniel Black', 350, 'Magical realms', 'A spellbinding tale of magic and destiny', 'ENGLISH'),
       ('Whispers in the Shadows', 'Mystery', 'ADULT', 29.95, '2018-08-11', 'Sophia Green', 450, 'Intriguing suspense', 'A gripping mystery that keeps you guessing', 'ENGLISH'),
       ('The Starlight Sonata', 'Romance', 'ADULT', 21.75, '2011-05-15', 'Michael Rose', 320, 'Heartwarming love story', 'A beautiful journey of love and passion', 'ENGLISH'),
       ('Beyond the Horizon', 'Science Fiction', 'CHILD', 18.99, '2004-05-15', 'Alex Carter', 280, 'Interstellar adventure', 'An epic sci-fi adventure beyond the stars', 'ENGLISH'),
       ('Dancing with Shadows', 'Thriller', 'ADULT', 26.50, '2015-05-15', 'Olivia Smith', 380, 'Suspenseful twists', 'A thrilling tale of danger and intrigue', 'ENGLISH'),
       ('Voices in the Wind', 'Historical Fiction', 'ADULT', 32.00, '2017-05-15', 'William Turner', 500, 'Rich historical setting', 'A compelling journey through time', 'ENGLISH'),
       ('Serenade of Souls', 'Fantasy', 'TEEN', 15.99, '2013-05-15', 'Isabella Reed', 330, 'Enchanting realms', 'A magical fantasy filled with wonder', 'ENGLISH'),
       ('Silent Whispers', 'Mystery', 'ADULT', 27.50, '2021-05-15', 'Benjamin Hall', 420, 'Intricate detective work', 'A mystery that keeps you on the edge', 'ENGLISH'),
       ('Whirlwind Romance', 'Romance', 'OTHER', 23.25, '2022-05-15', 'Emma Turner', 360, 'Passionate love affair', 'A romance that sweeps you off your feet', 'ENGLISH');